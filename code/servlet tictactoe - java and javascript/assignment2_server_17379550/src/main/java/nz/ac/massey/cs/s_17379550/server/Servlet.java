/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.massey.cs.s_17379550.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Sam
 */
@WebServlet(name = "Servlet", urlPatterns = {"/istart", "/ustart", "/move/*", "/state", "/won", "/possiblemoves"})
public class Servlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getRequestURI().matches("/ttt/istart")) {
            start(request, response, "p");
        }
        else if (request.getRequestURI().matches("/ttt/ustart")) {
            start(request, response, "c");
            HttpSession session = request.getSession();
            String[][] game = (String[][]) session.getAttribute("game");
            doMove(game, 1, 1);
        }
        else if (request.getRequestURI().matches("/ttt/move/x.y.*$")) {
            move(request, response);
        }
        else if (request.getRequestURI().matches("/ttt/state.*$")) {
            state(request, response);
        }
        else if (request.getRequestURI().matches("/ttt/won.*$")) {
            won(request, response);
        }
        else if (request.getRequestURI().matches("/ttt/possiblemoves.*$")) {
            possiblemoves(request, response);
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void start(HttpServletRequest request, HttpServletResponse response, String turn) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String[][] game = {{"_", "_", "_"},
                           {"_", "_", "_"},
                           {"_", "_", "_"}, {turn}};
        session.setAttribute("game", game);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(session.getId());
        }
    }

    private void move(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String[] urlsplit = request.getRequestURI().split("move/");
            String[] coords = urlsplit[urlsplit.length - 1].split("");
            try {
                String[][] game = (String[][]) session.getAttribute("game");
                int x = Integer.parseInt(coords[1]);
                int y = Integer.parseInt(coords[3]);
                
                //check if move is legal
                ArrayList<String> moves = findMoves(game);
                if (moves.contains(x + "," + y)) {
                    doMove(game, x, y);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.sendRedirect("/ttt/TTT.jsp;jsessionid=" + session.getId());
                }
                else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void state(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO image format
        HttpSession session = request.getSession(false);
        if (session != null) {
            String format = request.getParameter("format");
            String[][] game = (String[][]) session.getAttribute("game");
            
            if (format != null) {
                if (format.equals("txt")) {
                    response.setContentType("text/plain;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    try (PrintWriter out = response.getWriter()) {
                        for (int y = 0; y < 3; y++) {
                            for (int x = 0; x < 3; x++) {
                                out.print(game[y][x]);
                            }
                            out.print("\n");
                        }
                    }
                    catch (NullPointerException e) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }

                else if (format.equals("png")) {
                    response.setContentType("image/png");
                    response.setStatus(HttpServletResponse.SC_OK);

                    int width = 300;
                    int height = 300;
                    
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = img.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 48));
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.setColor(Color.BLACK);
                    g2d.setBackground(Color.WHITE);
                    g2d.clearRect(0, 0, width, height);
                    
                    //main border
                    g2d.drawRect(0, 0, width, height);
                    //down lines
                    g2d.drawLine(width / 3, 0, width / 3, height);
                    g2d.drawLine(2 * width / 3, 0, 2 * width / 3, height);
                    //across lines
                    g2d.drawLine(0, height / 3, width, height / 3);
                    g2d.drawLine(0, 2 * height / 3, width, 2 * height / 3);
                    
                    //moves
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {
                            g2d.drawString(game[y][x], x * width / 3 + fm.getWidths()[0], y * height / 3 + fm.getAscent());
                        }
                    }
                    
                    g2d.dispose();
                    
                    try {
                        ImageIO.write(img, "png", response.getOutputStream());
                    } catch (IOException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void won(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String[][] game = (String[][]) session.getAttribute("game");
            String winner = findWinner(game);

            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()) {
                out.print(winner);
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void possiblemoves(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String[][] game = (String[][]) session.getAttribute("game");
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            ArrayList<String> moves = findMoves(game);
            try (PrintWriter out = response.getWriter()) {
                for (String move : moves) {
                    out.println(move);
                }
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void doMove(String[][] game, int x, int y) {
        //make player move
        if (game[3][0].equals("p")) {
            game[y - 1][x - 1] = "x";
            game[3][0] = "c";
        }

        //computer makes move, only if isnt won yet
        if (findWinner(game).equals("none")) {
            if (game[3][0].equals("c")) {
                ArrayList<String> moves = findMoves(game);

                if (moves.size() > 0){
                    Random random = new Random();
                    String pcmove = moves.get(random.nextInt(moves.size()));
                    String[] pccoords = pcmove.split(",");

                    game[Integer.parseInt(pccoords[1]) - 1][Integer.parseInt(pccoords[0]) - 1] = "o";

                    game[3][0] = "p";
                }
            }
        }
    }

    private String findWinner(String[][] game) {
        int[][] lines = {{0, 0, 0, 1, 0, 2},
                         {0, 0, 1, 0, 2, 0},
                         {2, 2, 2, 1, 2, 0},
                         {2, 2, 1, 2, 0, 2},
                         {1, 1, 0, 1, 2, 1},
                         {1, 1, 1, 0, 1, 2},
                         {1, 1, 0, 2, 2, 0},
                         {1, 1, 0, 0, 2, 2}};

        //check all possible wining lines
        for (int[] coords : lines) {
            //check if 3 squares in a row are all the same
            if (game[coords[0]][coords[1]].equals(game[coords[2]][coords[3]]) && game[coords[0]][coords[1]].equals(game[coords[4]][coords[5]])) {
                if (game[coords[0]][coords[1]].equals("x")) {
                    return "user";
                }
                else if (game[coords[0]][coords[1]].equals("o")) {
                    return "computer";
                }
            }
        }
        
        //if get to here its either a draw or none
        ArrayList<String> moves = findMoves(game);
        if (moves.size() < 1) {
            //if no moves left its a draw
            return "draw";
        }
        else {
            return "none";
        }
    }

    private ArrayList<String> findMoves(String[][] game) {
        ArrayList<String> moves = new ArrayList();

        //get possible moves
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (game[y][x].equals("_")) {
                    moves.add((x + 1) + "," + (y + 1));
                }
            }
        }
        
        return moves;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
