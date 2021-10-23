/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE POWER_STATUS PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * Power supply status
 */
public class msg_power_status extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_POWER_STATUS = 125;
    public static final int MAVLINK_MSG_LENGTH = 6;
    private static final long serialVersionUID = MAVLINK_MSG_ID_POWER_STATUS;

      
    /**
     * 5V rail voltage.
     */
    public int Vcc;
      
    /**
     * Servo rail voltage.
     */
    public int Vservo;
      
    /**
     * Bitmap of power supply status flags.
     */
    public int flags;
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_POWER_STATUS;
        
        packet.payload.putUnsignedShort(Vcc);
        packet.payload.putUnsignedShort(Vservo);
        packet.payload.putUnsignedShort(flags);
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a power_status message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.Vcc = payload.getUnsignedShort();
        this.Vservo = payload.getUnsignedShort();
        this.flags = payload.getUnsignedShort();
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_power_status() {
        this.msgid = MAVLINK_MSG_ID_POWER_STATUS;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_power_status( int Vcc, int Vservo, int flags) {
        this.msgid = MAVLINK_MSG_ID_POWER_STATUS;

        this.Vcc = Vcc;
        this.Vservo = Vservo;
        this.flags = flags;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_power_status( int Vcc, int Vservo, int flags, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_POWER_STATUS;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.Vcc = Vcc;
        this.Vservo = Vservo;
        this.flags = flags;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_power_status(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_POWER_STATUS;
        
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
        return "MAVLINK_MSG_ID_POWER_STATUS - sysid:"+sysid+" compid:"+compid+" Vcc:"+Vcc+" Vservo:"+Vservo+" flags:"+flags+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_POWER_STATUS";
    }
}
        