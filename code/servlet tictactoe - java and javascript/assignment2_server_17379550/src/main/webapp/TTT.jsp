<%-- 
    Document   : TTT
    Created on : 23/05/2020, 3:07:33 PM
    Author     : Sam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="/ttt/style.css"/>
        <script>
            function newGame(start) {
                var newReq = new XMLHttpRequest();
                newReq.onreadystatechange = function() {
                    if (newReq.readyState === 4) {
                        var sess = newReq.responseText;
                        window.location.href = "/ttt/TTT.jsp;jsessionid=" + sess;
                    }
                };
                newReq.open("POST", "/ttt/" + start, true);
                newReq.send(null);
            }
            
            function setUp(session) {
                if (session === null) {
                    //start new game/state
                    var startReq = new XMLHttpRequest();
                    startReq.onreadystatechange = function() {
                        if (startReq.readyState === 4) {
                            var newSession = startReq.responseText;
                            setUp(newSession);
                        }
                    };
                    startReq.open("POST", "/ttt/istart", true);
                    startReq.send(null);
                }
                else {
                    //get won
                    var wonReq = new XMLHttpRequest();
                    wonReq.onreadystatechange = function() {
                        if (wonReq.readyState === 4) {
                            var winner = wonReq.responseText;
                            
                            //get state
                            var stateReq = new XMLHttpRequest();
                            stateReq.onreadystatechange = function() {
                                if (stateReq.readyState === 4) {
                                    var state = stateReq.responseText;

                                    //draw board
                                    var rows = state.split("\n");
                                    var y;
                                    for (y = 0; y < 3; y++) {
                                        var spaces = rows[y].split("");
                                        var x;
                                        for (x = 0; x < 3; x++) {
                                            if (spaces[x] === "_") {
                                                if (winner === "none") {
                                                    document.getElementById("row" + y).innerHTML += '<td><a href="/ttt/move/x' + (x + 1) + 'y' + (y + 1) + ';jsessionid=' + session + '"><div class="space">' + spaces[x] + '</div></a></td>';
                                                }
                                                else {
                                                    document.getElementById("row" + y).innerHTML += '<td><div class="space">' + spaces[x] + '</div></td>';
                                                }
                                            }
                                            else {
                                                document.getElementById("row" + y).innerHTML += '<td><div class="space">' + spaces[x] + '</div></td>';
                                            }
                                        }
                                    }
                                    
                                    //create winner/buttons
                                    if (winner !== "none") {
                                        var tag = document.createElement("h1");
                                        var text;
                                        if (winner === "draw") {
                                            text = document.createTextNode("Draw!");
                                        }
                                        else {
                                            text = document.createTextNode(winner + " wins!");
                                        }
                                        tag.appendChild(text);
                                        document.body.appendChild(tag);
                                        
                                        var istart = document.createElement("button");
                                        istart.addEventListener('click', function() { newGame("istart"); }, false);
                                        istart.innerHTML = "User Start";
                                        document.body.appendChild(istart);
                                        
                                        var ustart = document.createElement("button");
                                        ustart.addEventListener('click', function() { newGame("ustart"); }, false);
                                        ustart.innerHTML = "PC Start";
                                        document.body.appendChild(ustart);
                                    }
                                }
                            };
                            stateReq.open("GET", "/ttt/state;jsessionid=" + session + "?format=txt", true);
                            stateReq.send(null);
                        }
                    };
                    wonReq.open("GET", "/ttt/won;jsessionid=" + session, true);
                    wonReq.send(null);
                }
            }
            
            var url = window.location.href;
            var sid = url.split("jsessionid=");
            if (sid.length < 2){
                setUp(null);
            }
            else {
                setUp(sid[1]);
            }
        </script>
    </head>
    <body>
        <h1>Tic Tac Toe</h1>
        <table>
            <tr id="row0">
                
            </tr>
            <tr id="row1">
                
            </tr>
            <tr id="row2">
                
            </tr>
        </table>
    </body>
</html>
