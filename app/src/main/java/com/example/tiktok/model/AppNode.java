package com.example.tiktok.model;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.Math.ceil;

public class AppNode implements Serializable {
//Consumer
Socket requestSocketCon = null;
    ObjectOutputStream outCon = null;
    ObjectInputStream inCon = null;
    DataInputStream disCon = null;
    int exist;
    static ArrayList<String> listOfVids = new ArrayList<String>();
    ChannelName channelRequested;
    int videoFileChunkSize = 512 * 1024;

    //Publisher
    private static String ip;
    private static String channelN;
    private static String path = "C:\\Users\\Zacharias\\AndroidStudioProjects\\TikTok\\app\\src\\main\\assets\\PublishersVideos";
    private static ChannelName channelName;
    private static boolean already = false;
    private static ArrayList<String> channel = new ArrayList<String>();
    private static HashMap<String, Value> channelValueMap = new HashMap<String, Value>();
    private static Map<String, ArrayList<String>> channelMap = new HashMap<String, ArrayList<String>>();
    //private static ServerSocket providerSocket;
    int port, BrokerPort1 = 1234, BrokerPort2 = 5678, BrokerPort3 = 9101;
    String BrokerIP1, BrokerIP2, BrokerIP3 = "127.0.0.1";
    ArrayList<Broker> brokerKeys = new ArrayList<Broker>();
    ServerSocket providerSocket = null;
    ObjectOutputStream out1, out2 = null;
    DataOutputStream dos =null;
    ObjectInputStream in1, in2 = null;
    ArrayList<PubThread> threadPub = new ArrayList<PubThread>();





    AppNode(String ip, int port,int use ,String channelN){


        if(use==1){
            this.ip = ip;
            this.port = port;
            this.channelN = channelN;
        }
        else{
            this.ip = ip;
            this.port = port;

        }

    }



    public static ArrayList<String> getListOfVids(){
        return listOfVids;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void init(int use) {
        if(use==1){
            channelName = new ChannelName(channelN);
            Path dirPath = Paths.get(path);
            try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) {
                for (Path file : dirPaths) { //kathe fakelos sto path
                    if (Files.isDirectory(file) && (file.toString().contains(channelName.getChannelName()))) {
                        Path CurrentFolderContent = Paths.get(path.concat("/").concat(file.getFileName().toString()));
                        try (DirectoryStream<Path> currentVids = Files.newDirectoryStream(CurrentFolderContent)) {//ola ta vid ston fakelo
                            for (Path videos : currentVids) {
                                if (videos.getFileName().toString().startsWith("-")) {
                                    already = true;
                                    channel.add(videos.getFileName().toString());
                                    VideoFile video = new VideoFile(videos.getFileName().toString(), channelName, videos.toString());
                                    Value value = new Value(video);
                                    //values.add(value);
                                    channelValueMap.put(video.getVideoName(), value);

                                }
                            }
                            channelMap.put(channelName.getChannelName(),channel);
                        }
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            System.out.println("Publisher connected!");
        }
        else{

            try {
                Broker temp = findBroker();
                System.out.println("Finding the right Broker");
                //this.requestSocketCon = new Socket(this.ip, this.port);
                this.requestSocketCon = new Socket(temp.ip, temp.port + 1);
                this.outCon = new ObjectOutputStream(this.requestSocketCon.getOutputStream());
                this.inCon = new ObjectInputStream((this.requestSocketCon.getInputStream()));
                this.disCon = new DataInputStream(this.requestSocketCon.getInputStream());
                //System.out.println("Check Init");
                System.out.println(requestSocketCon);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }


    }




public void setChannelRequested(ChannelName channel) {
    this.channelRequested = channel;
}


    public Broker findBroker() {
        AppNode pub = new AppNode("127.0.0.1", 1278, 1,channelRequested.getChannelName());
        try {
            pub.updateList();
            return pub.hashTopic(this.channelRequested);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("Couldn't find the right broker");
            return null;
        }
    }


        public void register(ChannelName channel, String ip, int port) {



            try {

                this.outCon.writeUTF(ip); //1
                this.outCon.writeInt(port); //2
                this.outCon.writeUTF(channel.getChannelName()); //3

                this.outCon.flush();
                int brokerResponsible = inCon.readInt(); // Extra

                int brokerport = this.inCon.readInt(); //1

                exist = this.inCon.readInt();// value

                Scanner userInput = new Scanner(System.in);
                if (exist == 0) {

                }

                if (exist == 1) {
                    int size = this.inCon.readInt(); //videos size
                    System.out.println("size: " + size);



                    for (int i = 0; i < size; i++) {
                        this.listOfVids.add(this.inCon.readUTF());//videos
                    }
                    System.out.println(this.listOfVids.toString());
                    boolean vidFlag = false;
                    String video = null;

                    while (!vidFlag) {
                        System.out.println("Select video");
                        video = userInput.nextLine();


                        for (int i = 0; i < listOfVids.size(); i++) {
                            if (video.equals(listOfVids.get(i))) {
                                vidFlag = true;
                                this.outCon.writeUTF(video); //Video Name
                                this.outCon.flush();

                                break;
                            }
                        }
                    }



                    VideoFile vf = new VideoFile(null, null);
                    Value v = new Value(vf);

                    playData(channel, v);

                } else {
                    System.out.println("The channel doesn't exist :( ");
                    System.out.println("You can try again :)");
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }


        public void playData(ChannelName channelName, Value value) {
            int numberOfChunks = 0;
            String str;

            try {


                str = inCon.readUTF(); //Play data

                numberOfChunks = inCon.readInt(); // Play data


                ArrayList<byte[]> chunkss = new ArrayList<byte[]>();


                for (int kappa = 0; kappa < numberOfChunks; kappa++) {

                    int size = disCon.readInt(); // readSize Broker

                    byte[] filechunk = new byte[size];

                    disCon.readFully(filechunk, 0, size); //read chunks consumer

                    chunkss.add(filechunk);

                }


                if (str == null) str = "videoReceived";
                FileOutputStream fos = new FileOutputStream(str.concat(".mp4"));



                byte[] newVideoFileChunk = new byte[chunkss.size() * videoFileChunkSize];
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                for (byte[] chunk : chunkss) {
                    byteStream.write(chunk);
                }
                newVideoFileChunk = byteStream.toByteArray();

                fos.write(newVideoFileChunk);
                fos.flush();
                fos.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }






    public static String getChannelName() {
        return channelN;
    }


    public void updateList() {
        Broker a = new Broker(BrokerIP1, BrokerPort1);
        Broker b = new Broker(BrokerIP2, BrokerPort2);
        Broker c = new Broker(BrokerIP3, BrokerPort3);
        brokerKeys.add(a);
        brokerKeys.add(b);
        brokerKeys.add(c);

    }

    public void sendUsers(){
        // for (Broker b : brokerKeys){
        try{
            Broker b = hashTopic(channelName);
            this.port = b.getBrokerPort();
            Socket broker = new Socket(b.getBrokerIP(), b.getBrokerPort());
            System.out.println("Finding the right broker " + b.getBrokerPort());
            try {
                int kappa = this.port+2;
                System.out.println("POrt "+kappa);
                this.providerSocket = new ServerSocket(this.port+2 , 10);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            //Socket broker = new Socket(this.ip, this.port); //Edw ginete h sindesi
            //  System.out.println("OUT2 HAS PORT "+broker);
            out2 = new ObjectOutputStream((broker.getOutputStream()));
            in2 = new ObjectInputStream(broker.getInputStream());
            out2.writeInt(0);
            out2.writeUTF(getPublisherIP());
            out2.writeInt(getPublisherPort());
            out2.writeObject(getChannelMap());
            out2.writeUTF(channelN);
            out2.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    //}




    public String getPublisherIP(){
        return this.ip;
    }
    public int getPublisherPort(){
        return this.port;
    }
    public Map<String, ArrayList<String>> getChannelMap() {
        return this.channelMap;
    }


    public void takeRequests(){
        while(true){
            Socket requestSocket = null;
            try{
                //   System.out.println("The PROVIDERSOCKET IS " + providerSocket );
                requestSocket = providerSocket.accept();
                System.out.println(requestSocket);
                //System.out.println("WAITITITITI");
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            }
            //System.out.println("Waiting to call pubthead");
            AppNode.PubThread pt = new AppNode.PubThread(requestSocket, this);
            pt.start();
            threadPub.add(pt);
            System.out.println(pt.isAlive());

            for(int k = threadPub.size()-1;k>-1;k--){
                System.out.println(!threadPub.get(k).isAlive());
                if(!threadPub.get(k).isAlive()){
                    try{

                        threadPub.get(k).join(1000);
                        threadPub.remove(k);
                    }
                    catch(InterruptedException interruptedException){
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }
    public class PubThread extends Thread implements Serializable{
        Socket requestSocket = null;
        AppNode publisher;


        public PubThread(Socket s, AppNode publisher){
            requestSocket = s;
            this.publisher = publisher;
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run(){
            try {
                //System.out.println("Inside the PubThread Method");
                //System.out.println(requestSocket);
                out1 = new ObjectOutputStream(requestSocket.getOutputStream());
                in1 = new ObjectInputStream(requestSocket.getInputStream());
                dos = new DataOutputStream(requestSocket.getOutputStream());
                //System.out.println("TEST IN1");

                    ChannelName channel = (ChannelName) in1.readObject(); // pull1

                //String channel = in1.readUTF();
                //System.out.println("HCHOHOHO "+ channel.getChannelName());
                //ChannelName channelOBJ = new ChannelName(channel);
                Value value = (Value) in1.readObject();// Pull2
                String name = value.getVideoFile().getVideoName(); //Pull
                push(channel, name);
            }
            catch(ClassNotFoundException | IOException e){
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void push(ChannelName channelName, String name) throws IOException {
        //System.out.println("Inside the push method of Publisher");
        int chunk_size = 512 * 1024;
        int counter = 1;



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Path path = Paths.get(channelValueMap.get(name).getVideoFile().getPath());
        //System.out.println(path.toString());
        File file = new File(path.toString());
        //byte[] chunk = new byte[chunk_size];
        int numberOfChunks = (int) ceil((float) file.length() / chunk_size);
        //System.out.println("test4 "+ numberOfChunks);
        this.out1.writeUTF(name); // Name
        //System.out.println("test5"+name);
        this.out1.writeInt(numberOfChunks); // Chunks
        this.out1.flush();
        FileInputStream fis = new FileInputStream(file);
        System.out.println("test6");



        byte[] videoFileChunk = new byte[(int)file.length()];
        ArrayList<byte[]> chunks = new ArrayList<byte[]>();
        int start = 0;
        while (start < videoFileChunk.length)
        {
            int end = Math.min(videoFileChunk.length, start + chunk_size);
            chunks.add(Arrays.copyOfRange(videoFileChunk, start, end));
            start += chunk_size;
        }

/*
        for(byte[] k: chunks){
            System.out.println("Chunk" + k.length);

        }
  */

        for(byte[] k: chunks){
            int size = k.length;
            dos.writeInt(size);
            dos.write(k);
            dos.flush();
        }

        System.out.println("Data Sent");

    }

    public Broker hashTopic(ChannelName channelName) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        String name = channelName.getChannelName();
        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1, namehash);
        BigInteger max = new BigInteger("-1");
        if (brokerKeys.size() == 0) {
            // updateList();
        }
        for (int i = 0; i < brokerKeys.size(); i++) {
            if (brokerKeys.get(i).calculateKeys().compareTo(max) == 1) {
                max = brokerKeys.get(i).calculateKeys();
                //System.out.println("Answer is 1____________________");
            }
        }
        ArrayList<BigInteger> keys = new ArrayList<>();
        for (int i = 0; i < brokerKeys.size() ; i++) {
            keys.add(brokerKeys.get(i).calculateKeys());
        }
        Collections.sort(keys);
        ArrayList<Broker> brokerNodes = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            for (int j = 0; j < brokerKeys.size(); j++) {
                if ((keys.get(i)).compareTo(brokerKeys.get(j).calculateKeys()) == 0) {
                    brokerNodes.add(brokerKeys.get(j));
                    //System.out.println("Answer is 0____________________");
                }
            }
        }
        System.out.println(keys.size()+" Keys size");
        BigInteger hashNumber = big1.mod(max);

        if((hashNumber.compareTo(keys.get(0)) == 1) && (hashNumber.compareTo(keys.get(1)) == -1)){
            return brokerNodes.get(1);
        }
        if((hashNumber.compareTo(keys.get(1)) == 1)&& (hashNumber.compareTo(keys.get(2)) == -1)){
            return brokerNodes.get(2);
        }
        return brokerNodes.get(0);



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {

        BufferedReader keyboard = new BufferedReader((new InputStreamReader(System.in)));
        String str = "127.0.0.1";
        System.out.println("Select 1 for Publisher, 0 for consumer");
        try {
            //String useSrt =  keyboard.readLine();
            String useSrt = args[0];
            int use = new Integer(useSrt);
            System.out.println(use!=1);
            System.out.println(use!=0);
            boolean check=true;
            while(check){
                if(use!=1 && use!=0){
                    System.out.println("Please type 1 or 0");
                    useSrt = keyboard.readLine();
                    use = new Integer(useSrt);
                    check=true;
                }
                else check=false;
            }
            use = new Integer(useSrt);

            if(use==1){
                String name = null;
                System.out.println("Select your channels name: ");
                /*try {
                    name = keyboard.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                name = args[1];
                AppNode p = new AppNode( "127.0.0.1", 1234,use,name); //8765
                p.init(use);
                p.updateList();
                p.sendUsers();
                p.takeRequests();
            }
            else if (use==0){
                AppNode cn = new AppNode(str, 5679,use,null);
                System.out.println("Ip :" + cn.ip + " Port : " + cn.port);
                System.out.println("Select channel");
                String channel = args[1];
                /*try {
                    channel = keyboard.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                ChannelName channelName = new ChannelName(channel);

                cn.setChannelRequested(channelName);
                cn.init(use);

                cn.register(channelName, cn.ip, cn.port);
                System.out.println(channel);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }


    }










}


















