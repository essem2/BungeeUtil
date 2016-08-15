package dev.wolveringer.BungeeUtil.packets;

import dev.wolveringer.BungeeUtil.packets.Abstract.PacketPlayIn;
import dev.wolveringer.packet.PacketDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PacketPlayOutPluginMessage extends Packet implements PacketPlayIn{
	private String channel;
	private ByteBuf data;
	private ByteBufOutputStream os;
	private ByteBufInputStream is;
	private int length = -1;
	
	@Override
	public void read(PacketDataSerializer s) {
		s.markReaderIndex();
		try{
			channel = s.readString(-1);
			if(s.readableBytes() + s.readerIndex() != s.writerIndex()){
				System.out.println("Incorrect length: "+(s.readableBytes() + s.readerIndex()+" - "+s.writerIndex()));
			}
		}catch(Exception e){
			channel = null;
			e.printStackTrace();
			s.resetReaderIndex();
		}
		length = Math.min(s.readableBytes(), s.writerIndex() - s.readerIndex());
		data = Unpooled.buffer(length);
		s.readBytes(data, length);
	}

	@Override
	public void write(PacketDataSerializer s) {
		if(channel != null)
			s.writeString(channel);
		try{
			s.ensureWritable(data.readableBytes(), true);
			data.resetReaderIndex();
			data.readBytes(s, data.readableBytes());
			data.release();
		}catch(Exception e){
			System.out.println("Buffer: "+data+" - "+data.readableBytes()+" - "+length);
			throw e;
		}
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public ByteBufInputStream getCopiedByteBufInputStream(){
		return new ByteBufInputStream(data.copy());
	}
	public ByteBufInputStream getByteBufInputStream(){
		if(is == null)
			is = new ByteBufInputStream(data);
		return is;
	}
	public ByteBufOutputStream getByteBufOutputStream(){
		if(os == null)
			os = new ByteBufOutputStream(data);
		return os;
	}
	public void setData(ByteBuf data) {
		this.data = data;
		this.os = null;
		this.is = null;
	}
}