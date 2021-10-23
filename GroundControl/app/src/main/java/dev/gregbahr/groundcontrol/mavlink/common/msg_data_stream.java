/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE DATA_STREAM PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * Data stream status information.
 */
public class msg_data_stream extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_DATA_STREAM = 67;
    public static final int MAVLINK_MSG_LENGTH = 4;
    private static final long serialVersionUID = MAVLINK_MSG_ID_DATA_STREAM;

      
    /**
     * The message rate
     */
    public int message_rate;
      
    /**
     * The ID of the requested data stream
     */
    public short stream_id;
      
    /**
     * 1 stream is enabled, 0 stream is stopped.
     */
    public short on_off;
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_DATA_STREAM;
        
        packet.payload.putUnsignedShort(message_rate);
        packet.payload.putUnsignedByte(stream_id);
        packet.payload.putUnsignedByte(on_off);
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a data_stream message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.message_rate = payload.getUnsignedShort();
        this.stream_id = payload.getUnsignedByte();
        this.on_off = payload.getUnsignedByte();
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_data_stream() {
        this.msgid = MAVLINK_MSG_ID_DATA_STREAM;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_data_stream( int message_rate, short stream_id, short on_off) {
        this.msgid = MAVLINK_MSG_ID_DATA_STREAM;

        this.message_rate = message_rate;
        this.stream_id = stream_id;
        this.on_off = on_off;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_data_stream( int message_rate, short stream_id, short on_off, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_DATA_STREAM;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.message_rate = message_rate;
        this.stream_id = stream_id;
        this.on_off = on_off;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_data_stream(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_DATA_STREAM;
        
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.isMavlink2 = mavLinkPacket.isMavlink2;
        unpack(mavLinkPacket.payload);
    }

          
    /**
     * Returns a string with the MSG name and data
     */
    @Override
    public String toString() {
        return "MAVLINK_MSG_ID_DATA_STREAM - sysid:"+sysid+" compid:"+compid+" message_rate:"+message_rate+" stream_id:"+stream_id+" on_off:"+on_off+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_DATA_STREAM";
    }
}
        