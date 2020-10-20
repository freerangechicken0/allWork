/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.massey.cs.s_17379550.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

/**
 *
 * @author Sam
 */
public class tttTest {
    public static final String SERVER_URL = "http://localhost:8084";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    
    public tttTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void istartTest() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "text/plain;charset=UTF-8");
        
        //i cant test if it returns a specific jsessionid but i can test that it returns something
        //a jsessionid should be 32 characters
        assertTrue(res.body().string().length() == 32);
        
    }
    
    @Test
    public void ustartTest() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/ustart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        
        //same tests as istart
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "text/plain;charset=UTF-8");
        assertTrue(res.body().string().length() == 32);
    }
    
    @Test
    public void moveTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/move/x1y1;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        //move doesnt return anything but the request it redirected to TTT.jsp so the response is the
        //html code of that which i cant test, but i can test that the content length is large
        //from running a few tests the content length seems like it might be 6202 consistantly
        //but im not 100% confident in this so > 3000 is good enough
        assertTrue(res.isSuccessful());
        assertTrue(res.body().contentLength() > 3000);
    }
    
    @Test
    public void moveBadRequestTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        //test for malformed
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/move/x1yz;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 400);
        
        //test for not a valid move
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/move/x1y4;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 400);
    }
    
    @Test
    public void moveNotFoundTest() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/move/x1y1").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 404);
    }
    
    @Test
    public void statePNGTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/state;jsessionid=" + session + "?format=png").newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "image/png");
    }
    
    @Test
    public void stateTXTTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/state;jsessionid=" + session + "?format=txt").newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "text/plain;charset=UTF-8");
        assertEquals(res.body().string(), "___\n___\n___\n");
    }
    
    @Test
    public void stateNotFoundTest() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/state?format=txt").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 404);
    }
    
    @Test
    public void stateBadRequestTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/state;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 400);
    }
    
    @Test
    public void wonNoneTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/won;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "text/plain;charset=UTF-8");
        assertEquals(res.body().string(), "none");
    }
    
    @Test
    public void wonUserComputerDrawTest() throws IOException {
        //by sending moves returned from possiblemoves i can simulate a game but i cannot predict the outcome,
        //only that it will either be a draw, user or computer returned
        
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        String winner = "";
        do {
            //get moves
            urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/possiblemoves;jsessionid=" + session).newBuilder();
            url = urlBuilder.build().toString();
            req = new Request.Builder().url(url).build();
            res = CLIENT.newCall(req).execute();
            String moves[] = res.body().string().split("\\r?\\n");
            String move[] = moves[0].split(",");
            
            //make move
            urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/move/x" + move[0] + "y" + move[1] + ";jsessionid=" + session).newBuilder();
            url = urlBuilder.build().toString();
            req = new Request.Builder().url(url).build();
            CLIENT.newCall(req).execute();
            
            //get won
            urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/won;jsessionid=" + session).newBuilder();
            url = urlBuilder.build().toString();
            req = new Request.Builder().url(url).build();
            res = CLIENT.newCall(req).execute();
            winner = res.body().string();
        }
        while (winner.equals("none"));
        
        //response should be one of the 3 outcomes other than none
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "text/plain;charset=UTF-8");
        assertTrue(winner.equals("user") || winner.equals("computer") || winner.equals("draw"));
    }
    
    @Test
    public void wonNotFoundTest() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/won").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 404);
    }
    
    @Test
    public void possiblemovesTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        //get possiblemoves
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/possiblemoves;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        
        //should return all squares
        assertTrue(res.isSuccessful());
        assertEquals(res.body().contentType().toString(), "text/plain;charset=UTF-8");
        assertEquals(res.body().string(), "1,1\r\n2,1\r\n3,1\r\n1,2\r\n2,2\r\n3,2\r\n1,3\r\n2,3\r\n3,3\r\n");
    }
    
    @Test
    public void possiblemovesMakeMoveTest() throws IOException {
        //get a session
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/istart").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        String session = res.body().string();
        
        //get possiblemoves
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/possiblemoves;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        String first = res.body().string();
        
        //make move
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/move/x1y1;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        CLIENT.newCall(req).execute();
        
        //get possiblemoves
        urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/possiblemoves;jsessionid=" + session).newBuilder();
        url = urlBuilder.build().toString();
        req = new Request.Builder().url(url).build();
        res = CLIENT.newCall(req).execute();
        String second = res.body().string();
        
        //after 1 move from each player possiblemoves should go down 2
        assertEquals(first.split("\n").length, 9);
        assertEquals(second.split("\n").length, 7);
    }
    
    @Test
    public void possiblemovesNotFoundTest() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/ttt/possiblemoves").newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        Response res = CLIENT.newCall(req).execute();
        
        assertTrue(res.code() == 404);
    }
}
