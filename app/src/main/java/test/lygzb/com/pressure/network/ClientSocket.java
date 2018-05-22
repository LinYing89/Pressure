package test.lygzb.com.pressure.network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 文件上传下载socket
 * Created by Administrator on 2016/3/20.
 */
public class ClientSocket {

	private String ip;

	private int port;

	private Socket socket = null;

	private DataInputStream dis = null;
	private DataOutputStream dos = null;

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}


	public ClientSocket(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * 创建socket连接
	 *
	 * @throws Exception
	 *             exception
	 */
	public void CreateConnection() throws Exception {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			if (socket != null)
				socket.close();
			throw e;
		} finally {
		}
	}

	public void sendByte(byte b) throws IOException{
		try {

			dos.writeByte(b);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (dos != null){
				dos.close();
			}
		}
	}

	public void shutDownConnection() {
		try {
			if (dos != null)
				dos.close();
			if (dis != null)
				dis.close();
			if (socket != null)
				socket.close();
		} catch (Exception e) {

		}
	}

}
