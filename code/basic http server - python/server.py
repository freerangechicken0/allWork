from socket import *
import xml.etree.ElementTree as ET
import time
import datetime


serverport = 8080
serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.bind(('', serverport))
serverSocket.listen(1)
print('The server is ready to receive messages')


def send_data(fn, cs):
    # chrome (what i used) always asked for a favicon so this will always throw filenotfound at least once
    try:
        # open requested file and read data
        file = open(fn, "rb")
        data = file.read()

        # send 200 OK response and data
        response = "HTTP /1.1 200 OK\r\n\r\n"
        cs.sendall(response.encode() + data)
    except FileNotFoundError:
        # send 404 not found
        response = "HTTP /1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<html><head></head><body><h1>404 Not Found</h1></body></html>"
        cs.sendall(response.encode())


def get_friends(cs):
    try:
        # open friends xml and create ET object
        tree = ET.parse("friends.xml")
        root = tree.getroot()
    except FileNotFoundError:
        # create no friends html string
        friends_html = '<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Friends</title></head><body><div><a href="update.html">Update</a><a href="friends.html">Friends</a></div><h2>You have no friends</h2></body></html>'

        response = "HTTP /1.1 200 OK\r\n\r\n" + friends_html
        cs.sendall(response.encode())
        return

    # start html string
    friends_html = '<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Friends</title></head><body><div><a href="update.html">Update</a><a href="friends.html">Friends</a></div>'

    for friend in root:
        # iterate through friends getting their required info
        name = friend[0].text
        ip = friend[1].text
        port = int(friend[2].text)

        try:
            # get status.xml
            sock = socket(AF_INET, SOCK_STREAM)
            sock.settimeout(1)
            sock.connect((ip, port))
            sock.sendall("GET /status.xml HTTP/1.1\r\n\r\n".encode())
            response = sock.recv(16384).decode('UTF-8')
            response = response.split("\r\n\r\n")
            header = response[0]
            data = response[1]
            sock.close()
            if header == "HTTP /1.1 200 OK":

                # get variables
                try:
                    updates = ET.fromstring(data)
                    already_liked = False
                    try:
                        last_status = updates[-1]
                        status = last_status[1].text
                        status_html = "<p>Last status: " + status + ", "
                        likes = last_status[2]
                        timestamp = last_status[0].text
                        timestamp_html = "<p>" + timestamp + "</p>"
                    except IndexError:
                        # for when there are no statuses
                        status_html = "<p>No statuses, "
                        likes = ET.fromstring("<likes/>")
                        timestamp_html = ""
                        already_liked = True

                    # create like button
                    # check if already liked
                    host_ip = gethostbyname(gethostname())
                    for like in likes:
                        if like.text == host_ip:
                            already_liked = True
                    if already_liked:
                        like = '<form enctype="text/plain" method="post"><input type="hidden" name="like" value="' + name + '"/><input type="submit" value="Like" disabled></form>'
                    else:
                        like = '<form enctype="text/plain" method="post"><input type="hidden" name="like" value="' + name + '"/><input type="submit" value="Like"></form>'

                    # get picture
                    sock = socket(AF_INET, SOCK_STREAM)
                    sock.settimeout(2)
                    sock.connect((ip, port))
                    sock.sendall("GET /picture.jpg HTTP/1.1\r\n\r\n".encode())
                    pic_response = b''
                    for i in range(0, 2):
                        # picture never fully comes in when there is one call so two need to be made
                        data = sock.recv(131072)
                        time.sleep(0.1)
                        pic_response += data
                    sock.close()

                    # cut off http header
                    split = pic_response.split(b'\r\n\r\n')
                    pheader = split[0].decode('UTF-8')
                    pic = split[1]

                    if pheader == "HTTP /1.1 200 OK":
                        # save picture
                        file = open(name + ".jpg", "wb")
                        file.write(pic)
                        file.close()

                        html = '<div><h2>' + name + '</h2><img src="' + name + '.jpg" width="200">' + status_html + str(len(likes)) + ' likes.</p>' + like + timestamp_html + '</div>'
                    else:
                        # anything other than 200 OK just goes to no picture
                        # theoretically this would only ever be 404 because 403 would get picked up
                        # when trying to get status.xml so it wouldn't get this far
                        html = '<div><h2>' + name + '</h2><p>Has no picture</p>' + status_html + str(len(likes)) + ' likes.</p>' + like + timestamp_html + '</div>'
                except ET.ParseError:
                    # status.xml response is broken somehow so just put unreachable
                    html = '<div><h2>' + name + '</h2><p>Unreachable</p></div>'
            elif header == "HTTP /1.1 403 Forbidden\r\nContent-Type: text/html":
                # forbidden so there not friends
                html = '<div><h2>' + name + '</h2><p>You are not on ' + name + "'s friend list</p></div>"
            else:
                # in case there is an unexpected response
                html = '<div><h2>' + name + '</h2><p>Unreachable</p></div>'
        except timeout:
            # when the friends server isnt on
            html = '<div><h2>' + name + '</h2><p>Unreachable</p></div>'

        friends_html += html

    # close html string and send
    friends_html += '</body></html>'
    response = "HTTP /1.1 200 OK\r\n\r\n" + friends_html
    cs.sendall(response.encode())


def submit_likes(cs, req):
    # just take the from POST data which is just the name
    name = req.split("like=")[-1].strip()
    ip = ""
    port = 0

    tree = ET.parse("friends.xml")
    root = tree.getroot()
    for friend in root:
        if name == friend[0].text:
            # theoretically this will always eventually be true so there doesnt need to be an else
            ip = friend[1].text
            port = int(friend[2].text)

    # get status.xml
    sock = socket(AF_INET, SOCK_STREAM)
    sock.settimeout(3)
    try:
        sock.connect((ip, port))
        sock.sendall("POST /status.xml HTTP/1.1\r\n\r\n".encode())
    except timeout:
        pass
    sock.close()

    get_friends(cs)


def process_likes(addr):
    # when the server gets sent a like from another server
    # parse current status.xml and add like into it
    tree = ET.parse("status.xml")
    root = tree.getroot()
    # friends can only like the most recent status so it will always be the last element
    status = root[-1]
    likes = status[-1]
    ip = ET.SubElement(likes, "ip")
    ip.text = addr[0]

    # write ET object back
    tree.write("status.xml")


def parse_get(fn, cs, addr):
    try:
        # figure out if local host or other server
        allowed = False
        if addr[0] == "127.0.0.1":
            # if its localhost it can have whatever file it wants
            allowed = True
        else:
            # if its not localhost, checks to see if the request came from a friend
            try:
                tree = ET.parse("friends.xml")
                root = tree.getroot()
                for friend in root:
                    ip = friend[1].text
                    if addr[0] == ip:
                        allowed = True
                        break
            except FileNotFoundError:
                # no friends list so the request cant be from a friend and allowed stays false
                pass
        if allowed:
            # it local host or friend so sends file
            if fn == "friends.html":
                get_friends(cs)
            else:
                send_data(fn, cs)
        else:
            # send 403 forbidden if not local host or friend
            response = "HTTP /1.1 403 Forbidden\r\nContent-Type: text/html\r\n\r\n<html><head></head><body><h1>403 Forbidden</h1></body></html>"
            cs.sendall(response.encode())
    except ConnectionAbortedError:
        print("Connection Closed, Reconnecting")


def new_update(fn, cs, req):
    # get the tree and root
    tree = ET.parse("status.xml")
    root = tree.getroot()

    # create new update
    update = ET.Element("update")
    timestamp = ET.SubElement(update, "timestamp")
    timestamp.text = datetime.datetime.fromtimestamp(time.time()).strftime("%Y-%m-%d %H:%M:%S")
    status = ET.SubElement(update, "status")
    status.text = req.split("status=")[-1].strip()
    likes = ET.SubElement(update, "likes")

    # append update to tree and write to file
    root.append(update)
    tree.write("status.xml")

    # send http data
    try:
        send_data(fn, cs)
    except ConnectionAbortedError:
        print("Connection Closed, Reconnecting")


while 1:
    # accept connection
    connectionSocket, address = serverSocket.accept()

    # timeout needed bc recv hangs sometimes and it screws the whole thing up
    connectionSocket.settimeout(1)
    try:
        request = connectionSocket.recv(1024).decode('UTF-8')
    except timeout:
        # if it times out goes back to the start of the loop
        continue

    # get needed info
    requestType = request.split()[0]
    filename = request.split()[1].split("/")[1]

    # process request depending on what it is
    if requestType == "GET":
        parse_get(filename, connectionSocket, address)
    elif requestType == "POST":
        if filename == "update.html":
            # for receiving a new update
            new_update(filename, connectionSocket, request)
        elif filename == "friends.html":
            # for receiving the like from the browser and sending to other server
            submit_likes(connectionSocket, request)
        elif filename == "status.xml":
            # for receiving like from other server a processing it
            process_likes(address)
    # not sure if closing it necessary but it still works with closing so ill leave it
    connectionSocket.close()
