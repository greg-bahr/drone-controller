/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE CHANGE_OPERATOR_CONTROL PACKING
package dev.gregbahr.groundcontrol.mavlink.common;
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage;
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkPayload;
        
/**
 * Request to control this MAV
 */
public class msg_change_operator_control extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL = 5;
    public static final int MAVLINK_MSG_LENGTH = 28;
    private static final long serialVersionUID = MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL;

      
    /**
     * System the GCS requests control for
     */
    public short target_system;
      
    /**
     * 0: request control of this MAV, 1: Release control of this MAV
     */
    public short control_request;
      
    /**
     * 0: key as plaintext, 1-255: future, different hashing/encryption variants. The GCS should in general use the safest mode possible initially and then gradually move down the encryption level if it gets a NACK message indicating an encryption mismatch.
     */
    public short version;
      
    /**
     * Password / Key, depending on version plaintext or encrypted. 25 or less characters, NULL terminated. The characters may involve A-Z, a-z, 0-9, and "!?,.-"
     */
    public byte passkey[] = new byte[25];
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL;
        
        packet.payload.putUnsignedByte(target_system);
        packet.payload.putUnsignedByte(control_request);
        packet.payload.putUnsignedByte(version);
        
        for (int i = 0; i < passkey.length; i++) {
            packet.payload.putByte(passkey[i]);
        }
                    
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a change_operator_control message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
        this.target_system = payload.getUnsignedByte();
        this.control_request = payload.getUnsignedByte();
        this.version = payload.getUnsignedByte();
         
        for (int i = 0; i < this.passkey.length; i++) {
            this.passkey[i] = payload.getByte();
        }
                
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_change_operator_control() {
        this.msgid = MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_change_operator_control( short target_system, short control_request, short version, byte[] passkey) {
        this.msgid = MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL;

        this.target_system = target_system;
        this.control_request = control_request;
        this.version = version;
        this.passkey = passkey;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_change_operator_control( short target_system, short control_request, short version, byte[] passkey, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.target_system = target_system;
        this.control_request = control_request;
        this.version = version;
        this.passkey = passkey;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_change_operator_control(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL;
        
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.isMavlink2 = mavLinkPacket.isMavlink2;
        unpack(mavLinkPacket.payload);
    }

           
    /**
    * Sets the buffer of this message with a string, adds the necessary padding
    */
    public void setPasskey(String str) {
        int len = Math.min(str.length(), 25);
        for (int i=0; i<len; i++) {
            passkey[i] = (byte) str.charAt(i);
        }

        for (int i=len; i<25; i++) {            // padding for the rest of the buffer
            passkey[i] = 0;
        }
    }

    /**
    * Gets the message, formated as a string
    */
    public String getPasskey() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 25; i++) {
            if (passkey[i] != 0)
                buf.append((char) passkey[i]);
            else
                break;
        }
        return buf.toString();

    }
                         
    /**
     * Returns a string with the MSG name and data
     */
    @Override
    public String toString() {
        return "MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL - sysid:"+sysid+" compid:"+compid+" target_system:"+target_system+" control_request:"+control_request+" version:"+version+" passkey:"+passkey+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_CHANGE_OPERATOR_CONTROL";
    }
}
        