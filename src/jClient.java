//Geoffrey Kuhn

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//this class makes request to the server
class jClient implements ActionListener, Serializable {

    Socket Socket;       //Socket object
    ObjectOutputStream cout;
    ObjectInputStream cin;

    JTextField field;
    JTextField field2;
    JTextField field3;
    JTextField roots_field;

    double[] coeff_array = new double[3]; //stores coeffs
    double[] root_array_print = new double[3];

    jClient() {
        JFrame frame_obj = new JFrame("Quadratic roots finder");
        frame_obj.setLocation(200, 200);
        frame_obj.setLayout(new BorderLayout());
        frame_obj.setSize(600, 200);         //fram size
        frame_obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        field = new JTextField(10);          //text fields
        field2 = new JTextField(10);
        field3 = new JTextField(10);
        roots_field = new JTextField(10);
        roots_field.setEditable(false);        //uneditable last one

        // coefficient input fields
        JPanel field_obj = new JPanel();
        field_obj.setLayout(new GridLayout(3, 3));
        field_obj.add(new JLabel("Coefficient a:"));
        field_obj.add(field);
        field.addActionListener(this);
        field_obj.add(new JLabel("Coefficient b"));
        field_obj.add(field2);
        field2.addActionListener(this);
        field_obj.add(new JLabel("Coefficient c:"));
        field_obj.add(field3);
        field.addActionListener(this);
        frame_obj.add(field_obj, BorderLayout.NORTH);

        // button to send to server
        JPanel button_obj = new JPanel();
        button_obj.setLayout(new FlowLayout());
        button_obj.add(new JLabel("Calculate Roots"));
        JButton submitButton = new JButton("Send Coeffs");
        submitButton.addActionListener(this);
        button_obj.add(submitButton);
        frame_obj.add(button_obj, BorderLayout.CENTER);//adds to frame

        // display roots uneditable field
        JPanel roots_obj= new JPanel();
        roots_obj.setLayout(new GridLayout(1, 2));
        roots_obj.add(new JLabel("Roots returned"));
        roots_obj.add(roots_field);
        frame_obj.add(roots_obj, BorderLayout.SOUTH);

        frame_obj.setVisible(true);    //displays fields
    }

    public void actionPerformed(ActionEvent e)           //event handler
    {
        if(e.getActionCommand().equals("Send Coeffs"))   //action event for button
        {
            try {
                Socket = new Socket("localhost", 6789);  //creates socket
                System.out.println("Found localhost with port 6789");   //prints connection verification

                cin = new ObjectInputStream(Socket.getInputStream());   //create streams for in and out
                cout = new ObjectOutputStream(Socket.getOutputStream());
                try {

                    coeff_array[0] = Double.parseDouble(field.getText().trim());   //parse from text fields
                    coeff_array[1] = Double.parseDouble(field2.getText().trim());
                    coeff_array[2] = Double.parseDouble(field3.getText().trim());

                    sendDouble(coeff_array); //method for sending data to server

                    double[] root_array = (double[])cin.readObject();
                    //double[] root_array_print = new double[3];
                    for (int count = 0; count < 3; count++) {
                        root_array_print[count] = Double.valueOf(root_array[count]);
                    }
                    String no_roots = "No roots, don't enter zero or leave blank";
                    String root_string ="Root1: "+ root_array_print[0] + " Root2: "  + root_array_print[1];
                    //This is the flag sent from the server if there is a=0 or a field left blank
                    if (root_array[0]!=-.001){
                        roots_field.setText(root_string);   //sets field to correct roots if solution
                    }  else{
                        roots_field.setText(no_roots);       //sets field to say no solution if flag if true from server
                    }

                } catch (Exception e1) {
                }
            } catch (UnknownHostException e2) {
                System.out.println("Use a running host");
                System.out.println("Error: " + e2.toString());
            } catch (IOException ioException) {
                System.out.println("Error: " + ioException.toString());
                ioException.printStackTrace();
            } finally {
                try {
                    cin.close();    ///ends stream
                    cout.close();
                    Socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }}

    public static void main(String args[]) {    // starts program
        new jClient();
    }

    void sendDouble(double[] data)   //method for sending object
    {
        try {
            cout.writeObject(data);
            cout.flush();    //clears stream
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}