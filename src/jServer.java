//Geoffrey Kuhn

import java.io.*;
import java.net.*;
//this class sends data for client requests with serialization
class jServer implements Serializable {
    ServerSocket Socket;     //Socket object
    Socket connection = null;
    ObjectInputStream sin;     //sever input object
    ObjectOutputStream sout;    //server output object

    double roots[] = new double[3];
    double[] root_array = new double[3];

    void run()
    {
        try {
            Socket = new ServerSocket(6789, 10); //socket made
            System.out.println("Looking for a client");     //conenct flag will only make it print once
            connection = Socket.accept(); //accept socket
            System.out.println("Client found at " + connection.getInetAddress().getHostName());  //print address and hostname
            sout = new ObjectOutputStream(connection.getOutputStream());   //stream objects
            sin = new ObjectInputStream(connection.getInputStream());

            try {
                double[] coeff_array = (double[])sin.readObject();   //reads in from client
                root_array = rootCalc(coeff_array);
                sendData(root_array); // method to send back data to client
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
                e.printStackTrace();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally { // after processing finishes
            try {
                sin.close();
                sout.close();    //end stream
                Socket.close();  //socket exception
            } catch (IOException ioException) {
                System.out.println("Error: " + ioException.toString());//exception for io
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        jServer server = new jServer();
        while (true) {
            server.run(); //starts the server
        }
    }

    void sendData(double[] data) {   //sends back to client
        try {
            sout.writeObject(data);             //writes serialized out to client
            sout.flush();
        } catch (IOException ioException) {
            System.out.println("Error: " + ioException.toString());
            ioException.printStackTrace();
        }
    }

    double[] rootCalc(double coeffs[]) {   //method to calcualte roots
        roots[0] = (-coeffs[1] + Math.sqrt(coeffs[1] * coeffs[1] - 4 * coeffs[0] * coeffs[2])) / (2 * coeffs[0]);
        roots[1] = (-coeffs[1] - Math.sqrt(coeffs[1] * coeffs[1] - 4 * coeffs[0] * coeffs[2])) / (2 * coeffs[0]);
        roots[2] = (coeffs[1] * coeffs[1] - 4 * coeffs[0] * coeffs[2] < 0) ? 0 : 1;
        if (roots[0]==(Double.NaN) || roots[1]==(Double.NaN) || coeffs[0]==0.0){      //flag for if a=0 sent from server
            roots[0]=-.001;
        }
        return roots;
    }
}