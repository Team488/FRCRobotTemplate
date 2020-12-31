package competition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import xbot.common.controls.actuators.mock_adapters.MockCANTalon;

public class WebotsClient {
    String hostname = "localhost";
    InetAddress address;
    int supervisorPort = 10001;
    int robotPort = -1;
    byte[] buf;
    Socket socket;
    OutputStream output;
    InputStream input;
    PrintWriter writer;
    BufferedReader reader;

    HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build();

    public void initialize() {
        // Spawn a robot in the sim

        try {
            DatagramSocket udpSocket = new DatagramSocket();
            address = InetAddress.getByName(hostname);
            String msg = "spawn HttpRobotTemplate";
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, supervisorPort);
            udpSocket.send(packet);

            packet = new DatagramPacket(buf, buf.length);
            udpSocket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            robotPort = Integer.parseInt(received);
            udpSocket.close();

            socket = new Socket(address, robotPort);
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public JSONObject buildMotorObject(String motor, float value) {
        JSONObject result = new JSONObject();
        result.put("id", motor);
        result.put("val", value);
        return result;
    }

    public void sendMotors(List<MockCANTalon> motors) {
        JSONObject data = new JSONObject();
        List<JSONObject> motorValues = new ArrayList<JSONObject>();

        for(MockCANTalon motor : motors) {
            motorValues.add(buildMotorObject("Motor" + motor.deviceId, (float)motor.getThrottlePercent()));
        }

        data.put("motors", motorValues);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:" + robotPort + "/motors"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
