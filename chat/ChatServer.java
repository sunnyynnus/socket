package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServer {

	private static final Lock lock = new ReentrantLock();
	private static final Set<String> nameSet= new HashSet<>();
	private static final Set<PrintWriter> pwSet= new HashSet<>(); 
	private static final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public static void main(String[] args) throws IOException {
		try(ServerSocket listener= new ServerSocket(9123)){
			while(true){
				threadPool.submit(new ChatServerHandler(listener.accept()));
			}
		}

	}
	
	private static class ChatServerHandler implements Runnable{
		
		private final Socket socket;
		private String name;
		private PrintWriter out;
		private BufferedReader br;
		
		ChatServerHandler(Socket socket){
			this.socket= socket;
		}
		
		@Override
		public void run() {
			try{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out= new PrintWriter(socket.getOutputStream(), true);
				
				while(true){
					out.println("EnterName");
					name= br.readLine();
					if(name==null || "".equals(name.trim())){
						return;
					}
					try{
						lock.lock();
						if(!nameSet.contains(name)){
							nameSet.add(name);
							break;
						}
					} finally{
						lock.unlock();
					}
				}
				
				out.println("NameAccepted");
				pwSet.add(out);
				
				
				while(true){
					String line= br.readLine();
					if(line==null || "".equals(line.trim())){
						return;
					}
					for(PrintWriter pw: pwSet){
						pw.println("Message "+ name+ ": "+line);
					}
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(name!=null){
					nameSet.remove(name);
				}
				if (out != null) {
                    pwSet.remove(out);
                    out.close();
                }
				try {
					socket.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
