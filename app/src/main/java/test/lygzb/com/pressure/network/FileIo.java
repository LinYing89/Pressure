package test.lygzb.com.pressure.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件上传下载类
 * Created by Administrator on 2016/3/20.
 */
public class FileIo {

	public static final byte SEND_FILE = 0x01;
	public static final byte RECEIVE_FILE = 0x02;
	public static final byte RECEIVE_OVER = 0x03;
	public static final byte SEND_FILE_START = 0x04;
	public static final byte RECEIVE_READY = 0x05;

	public static String ROOT_PATH;

	private DataInputStream dis;
	private DataOutputStream dos;

	public FileIo(DataInputStream dis, DataOutputStream dos){
		setDis(dis);
		setDos(dos);
	}

	public DataInputStream getDis() {
		return dis;
	}


	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}


	public DataOutputStream getDos() {
		return dos;
	}


	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public void sendByte(byte b) throws IOException{
		dos.writeByte(b);
		dos.flush();
	}

	public boolean readFile(String user) {
		DataOutputStream fileOut = null;
		try {
			sendByte(FileIo.SEND_FILE);
			//发送用户名密码
			dos.writeUTF(user);
			dos.flush();

			//String savePath = "F:\\";
			byte[] buf = new byte[8192];
			int passedLen = 0;
			long len = 0;

			//读取用户名密码
			//String user = dis.readUTF();

			//读取文件个数
			int count = dis.readInt();
			System.out.println("文件个数为：" + count + "\n");

			while(count-- > 0){
				//开始接收文件标志
				byte b = dis.readByte();

				//发送接收文件准备好
				sendByte(RECEIVE_READY);

				//读取文件名称
				String fileName = dis.readUTF();
				String savePath = ROOT_PATH + File.separator + fileName;
				System.out.println("文件名为：" + savePath);

				fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));

				//读取文件长度
				len = dis.readLong();
				System.out.println("文件长度为：" + len);
				System.out.println("开始接收文件");

				while(true){
					int read = 0;
					if(dis != null){
						read = dis.read(buf);
					}
					//已接收的文件长度
					passedLen += read;
					if(read == -1){
						passedLen = 0;

						//接收完毕，发送接收完毕标志
						sendByte(RECEIVE_OVER);
						break;
					}
					System.out.println("文件接收了" + (passedLen * 100L / len) + "%");

					//写入文件
					fileOut.write(buf, 0, read);
					if(passedLen >= len){
						passedLen = 0;
						sendByte(RECEIVE_OVER);
						break;
					}
				}
				System.out.println("接收完成，文件存为" + savePath);
				fileOut.close();
			}
		} catch (Exception e) {
			System.out.println("receive error!");
			if(null != fileOut) {
				try {
					fileOut.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}

	public boolean sendFile(File fi) {
		DataInputStream fis = null;
		try {
			System.out.println("文件长度:" + (int) fi.length());

			fis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(fi)));

			//发送文件名
			dos.writeUTF(fi.getName());
			dos.flush();

			//发送文件长度
			dos.writeLong((long) fi.length());
			dos.flush();

			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];

			while (true) {
				int read = fis.read(buf);

				if (read == -1) {
					break;
				}
				//发送文件内容
				dos.write(buf, 0, read);
			}
			dos.flush();

			// 注意关闭socket链接哦，不然客户端会等待server的数据过来，
			// 直到socket超时，导致数据不完整。
			fis.close();
			// socket.close();
			System.out.println(fi.getName() + "文件传输完成");
			//读取文件接收完毕标志
			byte b = dis.readByte();
			System.out.println("file end " + b);
		} catch (Exception e) {
			e.printStackTrace();
			if(null != fis) {
				try {
					fis.close();
				}catch (Exception ex){ex.printStackTrace();}
			}
			return false;
		}
		return true;
	}

	public void sendUser(String user) throws IOException{
		dos.writeUTF(user);
		dos.flush();
	}

	public boolean sendFiles(String user) {
		try {
			sendByte(FileIo.RECEIVE_FILE);

			File file = new File(ROOT_PATH);
			File[] fs = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getAbsolutePath().endsWith(".xml"));
				}
			});

			sendUser(user);

			dos.writeInt(fs.length);
			dos.flush();
			for (File f : fs) {
				//发送开始发送文件标志
				dos.writeByte(FileIo.SEND_FILE_START);
				dos.flush();

				byte b = dis.readByte();
				System.out.println("file start read " + b);

				if(!sendFile(f)){
					return false;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//更新进度
			}
			//closeSocket();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
