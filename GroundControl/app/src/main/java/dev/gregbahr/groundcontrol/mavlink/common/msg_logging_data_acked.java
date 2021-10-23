/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE LOGGING_DATA_ACKED PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * A message containing logged data which requires a LOGGING_ACK to be sent back
 */
public class msg_logging_data_acked extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_LOGGING_DATA_ACKED = 267;
    public static final int MAVLINK_MSG_LENGTH = 255;
    private static final long serialVersionUID = MAVLINK_MSG_ID_LOGGING_DATA_ACKED;

      
    /**
     * sequence number (can wrap)
     */
    public int sequence;
      
    /**
     * system ID of the target
     */
    public short target_system;
      
    /**
     * component ID of the target
     */
    public short target_component;
      
    /**
     * data length
     */
    public short length;
      
    /**
     * offset into data where first message starts. This can be used for recovery, when a previous message got lost (set to UINT8_MAX if no start exists).
     */
    public short first_message_offset;
      
    /**
     * logged data
     */
    public short data[] = new short[249];
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_LOGGING_DATA_ACKED;
        
        packet.payload.putUnsignedShort(sequence);
        packet.payload.putUnsignedByte(target_system);
        packet.payload.putUnsignedByte(target_component);
        packet.payload.putUnsignedByte(length);
        packet.payload.putUnsignedByte(first_message_offset);
        
        for (int i = 0; i < data.length; i++) {
            packet.payload.putUnsignedByte(data[i]);
        }
                    
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a logging_data_acked message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.sequence = payload.getUnsignedShort();
        this.target_system = payload.getUnsignedByte();
        this.target_component = payload.getUnsignedByte();
        this.length = payload.getUnsignedByte();
        this.first_message_offset = payload.getUnsignedByte();
         
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = payload.getUnsignedByte();
        }
                
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_logging_data_acked() {
        this.msgid = MAVLINK_MSG_ID_LOGGING_DATA_ACKED;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_logging_data_acked( int sequence, short target_system, short target_component, short length, short first_message_offset, short[] data) {
        this.msgid = MAVLINK_MSG_ID_LOGGING_DATA_ACKED;

        this.sequence = sequence;
        this.target_system = target_system;
        this.target_component = target_component;
        this.length = length;
        this.first_message_offset = first_message_offset;
        this.data = data;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_logging_data_acked( int sequence, short target_system, short target_component, short length, short first_message_offset, short[] data, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_LOGGING_DATA_ACKED;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.sequence = sequence;
        this.target_system = target_system;
        this.target_component = target_component;
        this.length = length;
        this.first_message_offset = first_message_offset;
        this.data = data;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_logging_data_acked(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_LOGGING_DATA_ACKED;
        
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
        return "MAVLINK_MSG_ID_LOGGING_DATA_ACKED - sysid:"+sysid+" compid:"+compid+" sequence:"+sequence+" target_system:"+target_system+" target_component:"+target_component+" length:"+length+" first_message_offset:"+first_message_offset+" data:"+data+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_LOGGING_DATA_ACKED";
    }
}
        