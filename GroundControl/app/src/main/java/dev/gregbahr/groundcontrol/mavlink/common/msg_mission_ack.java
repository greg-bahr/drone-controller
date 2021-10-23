/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE MISSION_ACK PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * Acknowledgment message during waypoint handling. The type field states if this message is a positive ack (type=0) or if an error happened (type=non-zero).
 */
public class msg_mission_ack extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_MISSION_ACK = 47;
    public static final int MAVLINK_MSG_LENGTH = 4;
    private static final long serialVersionUID = MAVLINK_MSG_ID_MISSION_ACK;

      
    /**
     * System ID
     */
    public short target_system;
      
    /**
     * Component ID
     */
    public short target_component;
      
    /**
     * Mission result.
     */
    public short type;
      
    /**
     * Mission type.
     */
    public short mission_type;
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_MISSION_ACK;
        
        packet.payload.putUnsignedByte(target_system);
        packet.payload.putUnsignedByte(target_component);
        packet.payload.putUnsignedByte(type);
        
        if (isMavlink2) {
             packet.payload.putUnsignedByte(mission_type);
            
        }
        return packet;
    }

    /**
     * Decode a mission_ack message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.target_system = payload.getUnsignedByte();
        this.target_component = payload.getUnsignedByte();
        this.type = payload.getUnsignedByte();
        
        if (isMavlink2) {
             this.mission_type = payload.getUnsignedByte();
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_mission_ack() {
        this.msgid = MAVLINK_MSG_ID_MISSION_ACK;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_mission_ack( short target_system, short target_component, short type, short mission_type) {
        this.msgid = MAVLINK_MSG_ID_MISSION_ACK;

        this.target_system = target_system;
        this.target_component = target_component;
        this.type = type;
        this.mission_type = mission_type;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_mission_ack( short target_system, short target_component, short type, short mission_type, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_MISSION_ACK;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.target_system = target_system;
        this.target_component = target_component;
        this.type = type;
        this.mission_type = mission_type;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_mission_ack(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_MISSION_ACK;
        
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
        return "MAVLINK_MSG_ID_MISSION_ACK - sysid:"+sysid+" compid:"+compid+" target_system:"+target_system+" target_component:"+target_component+" type:"+type+" mission_type:"+mission_type+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_MISSION_ACK";
    }
}
        