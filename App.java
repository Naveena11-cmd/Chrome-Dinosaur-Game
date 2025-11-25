import javax.swing.*;
public class App
{
    public static void main(String[] args) throws Exception
    {
        int boardWidth=800;
        int boardHeight=350;

        JFrame frame=new JFrame("Chrome Dinosaur");
        // frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ImageIcon image=new ImageIcon("./img/dino.png");
        frame.setIconImage(image.getImage());
    

        ChromeDinosaur cd=new ChromeDinosaur();
        frame.add(cd);
        frame.pack();
        cd.requestFocusInWindow();  
        frame.setVisible(true);
    }
}
