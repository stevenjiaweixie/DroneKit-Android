// MESSAGE GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS PACKING
package com.MAVLink.ardupilotmega;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
        
/**
* 
    		Requests the calibration status for all gimbal axes.  Should result in a GIMBAL_REPORT_AXIS_CALIBRATION_STATUS message being generated by the gimbal
    	
*/
public class msg_gimbal_request_axis_calibration_status extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS = 211;
    public static final int MAVLINK_MSG_LENGTH = 2;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS;


     	
    /**
    * System ID
    */
    public byte target_system;
     	
    /**
    * Component ID
    */
    public byte target_component;
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket();
        packet.len = MAVLINK_MSG_LENGTH;
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS;
        		packet.payload.putByte(target_system);
        		packet.payload.putByte(target_component);
        
        return packet;
    }

    /**
    * Decode a gimbal_request_axis_calibration_status message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        	    
        this.target_system = payload.getByte();
        	    
        this.target_component = payload.getByte();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_gimbal_request_axis_calibration_status(){
        msgid = MAVLINK_MSG_ID_GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_gimbal_request_axis_calibration_status(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS;
        unpack(mavLinkPacket.payload);        
    }

        
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_GIMBAL_REQUEST_AXIS_CALIBRATION_STATUS -"+" target_system:"+target_system+" target_component:"+target_component+"";
    }
}
        