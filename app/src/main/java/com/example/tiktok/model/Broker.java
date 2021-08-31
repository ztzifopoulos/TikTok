package com.example.tiktok.model;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Broker implements Serializable {
    String ip;
    int port;
    ServerSocket publisherProviderSocket;
    Socket publisherRequestSocket;
    ServerSocket consumerProviderSocket;
    Socket publisherConnection;
    Socket consumerConnection;
    ObjectOutputStream out1, out2, out3 = null;
    ObjectInputStream in1, in2, in3 = null;
    DataOutputStream dos =null;
    DataInputStream dis = null;
    Map<String, ArrayList<String>> mapReceived = new HashMap<String, ArrayList<String>>();
    Map<AppNode, Map<String, ArrayList<String>>> channels = new HashMap<AppNode, Map<String, ArrayList<String>>>();
    ArrayList<AppNode> registeredPublishers = new ArrayList<AppNode>();
    ArrayList<MyThread> threads = new ArrayList<MyThread>();
    boolean exist = false;
    ChannelName channelNameReceived = null;
    boolean p = false;
    BigInteger no;


    public Broker(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }




    public synchronized void init() {
        try {
            publisherProviderSocket = new ServerSocket(port, 10);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            System.out.println(port+1);
            consumerProviderSocket = new ServerSocket(port+1, 10);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("Broker Initialised!");



    }


    public class MyThread2 extends Thread implements Serializable {
        Broker b;


        public MyThread2(Broker b) {
            this.b = b;
        }
        public void run() {
            while (true) {
                try {
                    System.out.println("Waiting to accept");

                    Socket publisher = b.getPublisherProviderSocket().accept();


                    try {
                        //System.out.println("OutPut2 port "+ publisher);
                        b.setOut2(new ObjectOutputStream(publisher.getOutputStream()));
                        b.setIn2(new ObjectInputStream(publisher.getInputStream()));
                        int check = b.in2.readInt();
                        System.out.println(check);
                        if (check == 1) {
                            System.out.println("PubRequest");
                        } else {
                            String publisherIP = b.in2.readUTF();
                            int publisherPort = b.in2.readInt();
                            Map<String, ArrayList<String>> channelValueMap = (HashMap<String, ArrayList<String>>) b.in2.readObject();
                            String channelName = in2.readUTF();
                            b.setMapReceived((channelValueMap));
                            AppNode pn = new AppNode(publisherIP, publisherPort,1 ,channelName);
                            channels.put(pn, b.getMapReceived());
                            registeredPublishers.add(pn);
                            System.out.println("Registered Publishers: " + pn.getPublisherIP() + " " + pn.getPublisherPort());
                        }
                    } catch (IOException | ClassNotFoundException exception) {
                        exception.printStackTrace();
                    }
                    b.setChannels(channels);






                    /*int count =0;
                    for (Map.Entry<Publisher,Map<String, ArrayList<String>>> book: channels.entrySet()) {
                        System.out.println(book.getValue());
                        count++;
                        System.out.println(count);
                    }*/






                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }






    public void setChannelNameReceived(ChannelName channelName) {
        this.channelNameReceived = channelName;
    }



    public ChannelName getChannelNameReceived() {
        return channelNameReceived;
    }

    public ServerSocket getConsumerProviderSocket() {
        return this.consumerProviderSocket;
    }


    public int getBrokerPort() {
        return this.port;
    }
    public String getBrokerIP() {
        return this.ip;
    }




    public void setChannels(Map<AppNode, Map<String, ArrayList<String>>> channels) {
        for (Map.Entry<AppNode, Map<String, ArrayList<String>>> entry : channels.entrySet()) {
            this.channels.put(entry.getKey(), entry.getValue());
        }
        int count=0;
    }


    public Map getMapReceived() {
        return mapReceived;
    }


    public void setMapReceived(Map<String, ArrayList<String>> mapReceived) {
        this.mapReceived = mapReceived;
    }


    public ServerSocket getPublisherProviderSocket() {
        return publisherProviderSocket;
    }

    public void setOut1(ObjectOutputStream out1) {
        this.out1 = out1;
    }
    public void setOut2(ObjectOutputStream out2) {
        this.out2 = out2;
    }
    public void setOut3(ObjectOutputStream out3) {
        this.out3 = out3;
    }
    public void setIn1(ObjectInputStream in1) {
        this.in1 = in1;
    }
    public void setIn2(ObjectInputStream in2) {
        this.in2 = in2;
    }
    public void setIn3(ObjectInputStream in3) {
        this.in3 = in3;
    }



    public void startPubThread() {
        MyThread2 thread = new MyThread2(this);
        thread.start();
    }






    public void conRequest() {
        while (true) {
            Socket consumer = null;
            try {
                //System.out.println("Consumer Trying to connect to broker: ");
                if (getConsumerProviderSocket()!=null)
                    consumer = getConsumerProviderSocket().accept();
               // System.out.println("Consumer Coneahdh");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            //System.out.println("Consumer Coneahdh2");
            Socket finalConsumer = consumer;
            System.out.println("New Consumer notice");
            MyThread t = new MyThread(finalConsumer, this);
            t.start();
            threads.add(t);

            for (int k = threads.size() - 1; k > -1; k--) {
                if ((!threads.get(k).isAlive())) {
                    try {
                        threads.get(k).join(1000);
                        threads.remove(k);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }














    public static void main(String[] args) {
        BufferedReader keyboard = new BufferedReader((new InputStreamReader(System.in)));
        String portSt = null;
        System.out.println("Select the port: ");
        /*try {
            portSt = keyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        portSt = args[0];
        int port = Integer.parseInt(portSt);
        Broker a = new Broker("127.0.0.1", port);
        a.init();
        //Map<Integer, Map<String, ArrayList<String>>> channels = new HashMap<>();
        a.startPubThread();
        a.conRequest();
    }



    public class MyThread extends Thread implements Serializable {
        Socket s = null;
        Boolean flag = false;
        Broker b;
        int exit = 0;
        AppNode pub;

        public MyThread(Socket s, Broker b) {
            this.s = s;
            this.b = b;

        }
        public void stopThread() {
            this.exit = 1;
        }
        public void run() {
            try {
                try {

                    //System.out.println("OutPut1 Port"+s);
                    setOut1(new ObjectOutputStream(s.getOutputStream()));
                    setIn1(new ObjectInputStream(s.getInputStream()));
                    dos= new DataOutputStream(s.getOutputStream());
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                String consumerIP = in1.readUTF(); //1
                int consumerPort = in1.readInt(); //2
                System.out.println("Consumer connected!");
                System.out.println("Consumer IP: " + consumerIP + " Consumer Port: " + consumerPort);

                try {
                    AppNode pub = new AppNode("127.0.0.1",3783,1 ,"test");
                    String channel = in1.readUTF(); //3
                    //System.out.println(channel+"  Test for the channel");
                   ChannelName channelName = new ChannelName(channel);
                    setChannelNameReceived(channelName);
                    pub.updateList();
                    Broker brokerResponsible = pub.hashTopic(channelName);
                    int brokerResponsiblePort = brokerResponsible.getBrokerPort();
                    //System.out.println("Check Broker Extra");
                    out1.writeInt(brokerResponsiblePort); //Extra
                    out1.flush();
                    //System.out.println("Broker port and IP: " + b.ip + " " + b.port);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                //System.out.println(channelName.getChannelName()+"  Test for the channel");
                    boolean f = false;
                    for (Map.Entry<AppNode, Map<String, ArrayList<String>>> entry : channels.entrySet()) { //Kathe kanali
                        Map<String, ArrayList<String>> k = entry.getValue();
                        for (Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()) { //kathe video
                            System.out.println(entry2);
                            if (entry2.getKey() != null) {
                                if (entry2.getKey().equals(getChannelNameReceived().getChannelName()) && entry2.getKey() != null) {
                                    f = true;
                                    List<String> videos = entry2.getValue();
                                    if (!p) {
                                        out1.writeInt(port); //1
                                    }
                                    p = false;
                                    //while(true){
                                    out1.writeInt(1); //value

                                        out1.writeInt(videos.size()); //Videos Size
                                        out1.flush();
                                        for (int j = 0; j < videos.size(); j++) {
                                            out1.writeUTF(videos.get(j));//videos
                                            out1.flush();
                                        }
                                        String vid = in1.readUTF(); //video Name



                                        System.out.println("Video Selected: " + vid);
                                        pull(getChannelNameReceived(), vid);
                                        break;
                                    }

                            }
                            if (f) {

                                break;
                            }
                        }
                    }
                    if (!exist) {
                        out1.writeInt(port);
                        out1.writeInt(0);
                        out1.flush();
                    }

            } catch (IOException | NoSuchAlgorithmException exception) {
                exception.printStackTrace();
            }
        }
    }


    public synchronized void pull(ChannelName channel, String videoName) {
        AppNode pNode = null;
        for (Map.Entry<AppNode, Map<String, ArrayList<String>>> entry : channels.entrySet()) {
            Map<String, ArrayList<String>> k = entry.getValue();
            //System.out.println("Test Before if");
            //System.out.println(k);
            if (k.containsKey(channel.getChannelName())) {
                pNode = entry.getKey();
              //  System.out.println("MADAFAKAZ"+entry.getKey());
                break;
            }
        }
       // System.out.println(pNode.getPublisherPort());
        try {
            //System.out.println("testCheckkk");
            //System.out.println(pNode.getPublisherPort());
            //pNode = new Publisher("127.0.0.1",1236,"kappa");
            publisherConnection = new Socket(pNode.getPublisherIP(), pNode.getPublisherPort() + 2 );
            //System.out.println();
            //System.out.println("test1");
            System.out.println(publisherConnection);
            out3 = new ObjectOutputStream(publisherConnection.getOutputStream());
            in3 = new ObjectInputStream(publisherConnection.getInputStream());
            dis = new DataInputStream(publisherConnection.getInputStream());

            //setOut3(new ObjectOutputStream(publisherConnection.getOutputStream()));
            //setIn3(new ObjectInputStream(publisherConnection.getInputStream()));
            //System.out.println("test2");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        VideoFile v = new VideoFile(videoName, channel);
        Value value = new Value(v);


        try {
            //System.out.println("test3");
            out3.writeObject(channel);// Pull1
            //out3.writeUTF(channel.getChannelName());
            //System.out.println(channel.getChannelName());
            out3.writeObject(value);// pull2
            out3.flush();
            //System.out.println("test4");
            String vName = in3.readUTF();//Name
            //System.out.println("test5 "+vName);
            int numberOfChunks = in3.readInt(); //Chunks
            System.out.println(vName);
            System.out.println(numberOfChunks);









            ArrayList<byte[]> chunks = new ArrayList<byte[]>();
            for(int kappa=0; kappa<numberOfChunks; kappa++){
               // System.out.println("In broker for ");
                int size = dis.readInt(); // readSize Publisher
                //System.out.println("In broker for ");
                byte[] filechunk = new byte[size];
                //System.out.println("In broker for ");
                dis.readFully(filechunk,0,size); //read chunkc publisher
                //System.out.println("In broker for ");
                chunks.add(filechunk);
                System.out.println("In broker for ");
            }
            System.out.println("Dara Received");

            /*
            for(byte[] k: chunks){
                System.out.println("Chunk"+ k.length);
            }
*/








            out1.writeUTF(vName); // Play Data
            out1.writeInt(numberOfChunks);//Play data
            //out1.writeObject(value); // Play Data
            System.out.println("Number of file chunks: " + numberOfChunks);
            out1.flush();



            for(byte[] k: chunks){
                int size = k.length;
                System.out.println("Size "+ size );
                dos.writeInt(size); //write chunkssize consumer
                dos.write(k); // write chunnks consumer
                dos.flush();
            }

            System.out.println("Data Sent");








            try {
                for (int i = 1; i <= numberOfChunks; i++) {
                    //System.out.println("Inside for loop");
                    VideoFile vid = (VideoFile) in3.readObject();
                    System.out.println();
                    System.out.println("Sending video: " + vid.getVideoName());
                    out1.writeObject((VideoFile) vid);
                    out1.flush();
                    String good = in3.readUTF();
                }
                out3.close();
                in3.close();
                dis.close();
                publisherConnection.close();

            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



















    public BigInteger calculateKeys() {
        String s = ip + port;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(s.getBytes());
            no = new BigInteger(1, messageDigest);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
        //System.out.println(no);

        return no;
    }




}
