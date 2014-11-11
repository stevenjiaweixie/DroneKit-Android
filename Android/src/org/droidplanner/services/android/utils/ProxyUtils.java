package org.droidplanner.services.android.utils;

import android.util.Log;

import com.MAVLink.Messages.ardupilotmega.msg_mission_item;
import com.ox3dr.services.android.lib.drone.mission.item.MissionItem;
import com.ox3dr.services.android.lib.drone.mission.item.command.CameraTrigger;
import com.ox3dr.services.android.lib.drone.mission.item.command.ChangeSpeed;
import com.ox3dr.services.android.lib.drone.mission.item.command.EpmGripper;
import com.ox3dr.services.android.lib.drone.mission.item.command.ReturnToLaunch;
import com.ox3dr.services.android.lib.drone.mission.item.command.SetServo;
import com.ox3dr.services.android.lib.drone.mission.item.command.Takeoff;
import com.ox3dr.services.android.lib.drone.mission.item.command.YawCondition;
import com.ox3dr.services.android.lib.drone.mission.item.complex.CameraDetail;
import com.ox3dr.services.android.lib.drone.mission.item.complex.StructureScanner;
import com.ox3dr.services.android.lib.drone.mission.item.complex.Survey;
import com.ox3dr.services.android.lib.drone.mission.item.complex.SurveyDetail;
import com.ox3dr.services.android.lib.drone.mission.item.raw.MissionItemMessage;
import com.ox3dr.services.android.lib.drone.mission.item.spatial.Circle;
import com.ox3dr.services.android.lib.drone.mission.item.spatial.Land;
import com.ox3dr.services.android.lib.drone.mission.item.spatial.RegionOfInterest;
import com.ox3dr.services.android.lib.drone.mission.item.spatial.SplineWaypoint;
import com.ox3dr.services.android.lib.drone.mission.item.spatial.Waypoint;

import org.droidplanner.core.helpers.coordinates.Coord2D;
import org.droidplanner.core.helpers.units.Altitude;
import org.droidplanner.core.helpers.units.Length;
import org.droidplanner.core.helpers.units.Speed;
import org.droidplanner.core.mission.Mission;
import org.droidplanner.core.mission.commands.ConditionYaw;
import org.droidplanner.core.mission.commands.ReturnToHome;
import org.droidplanner.core.mission.survey.CameraInfo;
import org.droidplanner.core.mission.survey.SurveyData;

import java.util.List;

/**
 * Created by fhuya on 11/10/14.
 */
public class ProxyUtils {

    private static final String TAG = ProxyUtils.class.getSimpleName();

    public static CameraDetail getCameraDetail(CameraInfo camInfo) {
        if(camInfo == null) return null;
        return new CameraDetail(camInfo.name, camInfo.sensorWidth,
                camInfo.sensorHeight, camInfo.sensorResolution, camInfo.focalLength,
                camInfo.overlap, camInfo.sidelap, camInfo.isInLandscapeOrientation);
    }

    public static CameraInfo getCameraInfo(CameraDetail camDetail){
        if(camDetail == null) return null;

        CameraInfo camInfo = new CameraInfo();
        camInfo.name = camDetail.getName();
        camInfo.sensorWidth = camDetail.getSensorWidth();
        camInfo.sensorHeight = camDetail.getSensorHeight();
        camInfo.sensorResolution = camDetail.getSensorResolution();
        camInfo.focalLength = camDetail.getFocalLength();
        camInfo.overlap = camDetail.getOverlap();
        camInfo.sidelap = camDetail.getSidelap();
        camInfo.isInLandscapeOrientation = camDetail.isInLandscapeOrientation();

        return camInfo;
    }

    public static SurveyDetail getSurveyDetail(SurveyData surveyData) {
        SurveyDetail surveyDetail = new SurveyDetail();
        surveyDetail.setCameraDetail(getCameraDetail(surveyData.getCameraInfo()));
        surveyDetail.setSidelap(surveyData.getSidelap());
        surveyDetail.setOverlap(surveyData.getOverlap());
        surveyDetail.setAngle(surveyData.getAngle());
        surveyDetail.setAltitude(surveyData.getAltitude().valueInMeters());
        return surveyDetail;
    }

    public static org.droidplanner.core.mission.MissionItem getMissionItem(Mission mission,
                                                                           MissionItem proxyItem) {
        if (proxyItem == null)
            return null;

        org.droidplanner.core.mission.MissionItem missionItem;
        switch (proxyItem.getType()) {

            case CAMERA_TRIGGER: {
                CameraTrigger proxy = (CameraTrigger) proxyItem;

                org.droidplanner.core.mission.commands.CameraTrigger temp = new org.droidplanner
                        .core.mission.commands.CameraTrigger(mission,
                        new Length(proxy.getTriggerDistance()));

                missionItem = temp;
                break;
            }
            case CHANGE_SPEED: {
                ChangeSpeed proxy = (ChangeSpeed) proxyItem;

                org.droidplanner.core.mission.commands.ChangeSpeed temp = new org.droidplanner
                        .core.mission.commands.ChangeSpeed(mission, new Speed(proxy.getSpeed()));

                missionItem = temp;
                break;
            }
            case EPM_GRIPPER: {
                EpmGripper proxy = (EpmGripper) proxyItem;

                org.droidplanner.core.mission.commands.EpmGripper temp = new org.droidplanner
                        .core.mission.commands.EpmGripper(mission, proxy.isRelease());

                missionItem = temp;
                break;
            }
            case RETURN_TO_LAUNCH: {
                ReturnToLaunch proxy = (ReturnToLaunch) proxyItem;

                ReturnToHome temp = new ReturnToHome(mission);
                temp.setHeight(new Altitude(proxy.getReturnAltitude()));

                missionItem = temp;
                break;
            }
            case SET_SERVO: {
                SetServo proxy = (SetServo) proxyItem;

                org.droidplanner.core.mission.commands.SetServo temp = new org.droidplanner.core
                        .mission.commands.SetServo(mission, proxy.getChannel(), proxy.getPwm());

                missionItem = temp;
                break;
            }
            case TAKEOFF: {
                Takeoff proxy = (Takeoff) proxyItem;

                org.droidplanner.core.mission.commands.Takeoff temp = new org.droidplanner.core
                        .mission.commands.Takeoff(mission, new Altitude(proxy.getTakeoffAltitude()));

                missionItem = temp;
                break;
            }
            case CIRCLE: {
                Circle proxy = (Circle) proxyItem;

                org.droidplanner.core.mission.waypoints.Circle temp = new org.droidplanner.core
                        .mission.waypoints.Circle(mission, MathUtils.latLongAltToCoord3D(proxy
                        .getCoordinate()));
                temp.setRadius(proxy.getRadius());
                temp.setTurns(proxy.getTurns());

                missionItem = temp;
                break;
            }
            case LAND: {
                Land proxy = (Land) proxyItem;

                org.droidplanner.core.mission.waypoints.Land temp = new org.droidplanner.core
                        .mission.waypoints.Land(mission, MathUtils.latLongToCoord2D(proxy
                        .getCoordinate()));

                missionItem = temp;
                break;
            }
            case REGION_OF_INTEREST: {
                RegionOfInterest proxy = (RegionOfInterest) proxyItem;

                org.droidplanner.core.mission.waypoints.RegionOfInterest temp = new org
                        .droidplanner.core.mission.waypoints.RegionOfInterest(mission,
                        MathUtils.latLongAltToCoord3D(proxy.getCoordinate()));

                missionItem = temp;
                break;
            }
            case SPLINE_WAYPOINT: {
                SplineWaypoint proxy = (SplineWaypoint) proxyItem;

                org.droidplanner.core.mission.waypoints.SplineWaypoint temp = new org
                        .droidplanner.core.mission.waypoints.SplineWaypoint(mission,
                        MathUtils.latLongAltToCoord3D(proxy.getCoordinate()));
                temp.setDelay(proxy.getDelay());

                missionItem = temp;
                break;
            }
            case STRUCTURE_SCANNER: {
                StructureScanner proxy = (StructureScanner) proxyItem;

                org.droidplanner.core.mission.waypoints.StructureScanner temp = new org
                        .droidplanner.core.mission.waypoints.StructureScanner(mission,
                        MathUtils.latLongAltToCoord3D(proxy.getCoordinate()));
                temp.setRadius((int) proxy.getRadius());
                temp.setNumberOfSteps(proxy.getStepsCount());
                temp.setAltitudeStep((int) proxy.getHeightStep());
                temp.enableCrossHatch(proxy.isCrossHatch());
                temp.setCamera(getCameraInfo(proxy.getSurveyDetail().getCameraDetail()));

                missionItem = temp;
                break;
            }
            case WAYPOINT: {
                Waypoint proxy = (Waypoint) proxyItem;

                org.droidplanner.core.mission.waypoints.Waypoint temp = new org.droidplanner.core
                        .mission.waypoints.Waypoint(mission, MathUtils.latLongAltToCoord3D(proxy
                        .getCoordinate()));
                temp.setAcceptanceRadius(proxy.getAcceptanceRadius());
                temp.setDelay(proxy.getDelay());
                temp.setOrbitCCW(proxy.isOrbitCCW());
                temp.setOrbitalRadius(proxy.getOrbitalRadius());
                temp.setYawAngle(proxy.getYawAngle());

                missionItem = temp;
                break;
            }
            case SURVEY: {
                Survey proxy = (Survey) proxyItem;
                SurveyDetail surveyDetail = proxy.getSurveyDetail();
                List<Coord2D> polygonPoints = MathUtils.latLongToCoord2D(proxy.getPolygonPoints());

                org.droidplanner.core.mission.survey.Survey temp = new org.droidplanner.core
                        .mission.survey.Survey(mission, polygonPoints);

                if(surveyDetail != null) {
                    temp.update(surveyDetail.getAngle(), new Altitude(surveyDetail.getAltitude()),
                            surveyDetail.getOverlap(), surveyDetail.getSidelap());

                    CameraDetail cameraDetail = surveyDetail.getCameraDetail();
                    if(cameraDetail != null)
                        temp.setCameraInfo(getCameraInfo(cameraDetail));
                }

                try {
                    temp.build();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                missionItem = temp;
                break;
            }
            case YAW_CONDITION: {
                YawCondition proxy = (YawCondition) proxyItem;

                ConditionYaw temp = new ConditionYaw(mission, proxy.getAngle(), proxy.isRelative());
                temp.setAngularSpeed(proxy.getAngularSpeed());

                missionItem = temp;
                break;
            }

            default:
                missionItem = null;
                break;
        }

        return missionItem;
    }

    public static MissionItem getProxyMissionItem(org.droidplanner.core.mission.MissionItem item) {
        if (item == null)
            return null;

        MissionItem proxyMissionItem;
        switch (item.getType()) {
            case WAYPOINT: {
                org.droidplanner.core.mission.waypoints.Waypoint source = (org.droidplanner.core.mission.waypoints.Waypoint) item;

                Waypoint temp = new Waypoint();
                temp.setCoordinate(MathUtils.coord3DToLatLongAlt(source.getCoordinate()));
                temp.setAcceptanceRadius(source.getAcceptanceRadius());
                temp.setDelay(source.getDelay());
                temp.setOrbitalRadius(source.getOrbitalRadius());
                temp.setOrbitCCW(source.isOrbitCCW());
                temp.setYawAngle(source.getYawAngle());

                proxyMissionItem = temp;
                break;
            }

            case SPLINE_WAYPOINT: {
                org.droidplanner.core.mission.waypoints.SplineWaypoint source = (org.droidplanner.core.mission.waypoints.SplineWaypoint) item;

                SplineWaypoint temp = new SplineWaypoint();
                temp.setCoordinate(MathUtils.coord3DToLatLongAlt(source.getCoordinate()));
                temp.setDelay(source.getDelay());

                proxyMissionItem = temp;
                break;
            }

            case TAKEOFF: {
                org.droidplanner.core.mission.commands.Takeoff source = (org.droidplanner.core.mission.commands.Takeoff) item;

                Takeoff temp = new Takeoff();
                temp.setTakeoffAltitude(source.getFinishedAlt().valueInMeters());

                proxyMissionItem = temp;
                break;
            }
            case RTL: {
                ReturnToHome source = (ReturnToHome) item;

                ReturnToLaunch temp = new ReturnToLaunch();
                temp.setReturnAltitude(source.getHeight().valueInMeters());

                proxyMissionItem = temp;
                break;
            }
            case LAND: {
                org.droidplanner.core.mission.waypoints.Land source = (org.droidplanner.core.mission.waypoints.Land) item;

                Land temp = new Land();
                temp.setCoordinate(MathUtils.coord3DToLatLongAlt(source.getCoordinate()));

                proxyMissionItem = temp;
                break;
            }

            case CIRCLE: {
                org.droidplanner.core.mission.waypoints.Circle source = (org.droidplanner.core.mission.waypoints.Circle) item;

                Circle temp = new Circle();
                temp.setCoordinate(MathUtils.coord3DToLatLongAlt(source.getCoordinate()));
                temp.setRadius(source.getRadius());
                temp.setTurns(source.getNumberOfTurns());

                proxyMissionItem = temp;
                break;
            }

            case ROI: {
                org.droidplanner.core.mission.waypoints.RegionOfInterest source = (org.droidplanner.core.mission.waypoints.RegionOfInterest) item;

                RegionOfInterest temp = new RegionOfInterest();
                temp.setCoordinate(MathUtils.coord3DToLatLongAlt(source.getCoordinate()));

                proxyMissionItem = temp;
                break;
            }

            case SURVEY: {
                org.droidplanner.core.mission.survey.Survey source = (org.droidplanner.core.mission.survey.Survey) item;

                boolean isValid = true;
                try {
                    source.build();
                } catch (Exception e) {
                    isValid = false;
                }

                Survey temp = new Survey();
                temp.setValid(isValid);
                temp.setSurveyDetail(getSurveyDetail(source.surveyData));
                temp.setPolygonPoints(MathUtils.coord2DToLatLong(source.polygon.getPoints()));

                if(source.grid != null) {
                    temp.setGridPoints(MathUtils.coord2DToLatLong(source.grid.gridPoints));
                    temp.setCameraLocations(MathUtils.coord2DToLatLong(source.grid.getCameraLocations()));
                }

                temp.setPolygonArea(source.polygon.getArea().valueInSqMeters());

                proxyMissionItem = temp;
                break;
            }

            case CYLINDRICAL_SURVEY: {
                org.droidplanner.core.mission.waypoints.StructureScanner source = (org.droidplanner.core.mission.waypoints.StructureScanner) item;

                StructureScanner temp = new StructureScanner();
                temp.setSurveyDetail(getSurveyDetail(source.getSurveyData()));
                temp.setCoordinate(MathUtils.coord3DToLatLongAlt(source.getCoordinate()));
                temp.setRadius(source.getRadius().valueInMeters());
                temp.setCrossHatch(source.isCrossHatchEnabled());
                temp.setHeightStep(source.getEndAltitude().valueInMeters());
                temp.setStepsCount(source.getNumberOfSteps());
                temp.setPath(MathUtils.coord2DToLatLong(source.getPath()));

                proxyMissionItem = temp;
                break;
            }
            case CHANGE_SPEED: {
                org.droidplanner.core.mission.commands.ChangeSpeed source = (org.droidplanner.core.mission.commands.ChangeSpeed) item;

                ChangeSpeed temp = new ChangeSpeed();
                temp.setSpeed(source.getSpeed().valueInMetersPerSecond());

                proxyMissionItem = temp;
                break;
            }

            case CAMERA_TRIGGER: {
                org.droidplanner.core.mission.commands.CameraTrigger source = (org.droidplanner.core.mission.commands.CameraTrigger) item;

                CameraTrigger temp = new CameraTrigger();
                temp.setTriggerDistance(source.getTriggerDistance().valueInMeters());

                proxyMissionItem = temp;
                break;
            }
            case EPM_GRIPPER: {
                org.droidplanner.core.mission.commands.EpmGripper source = (org.droidplanner.core.mission.commands.EpmGripper) item;

                EpmGripper temp = new EpmGripper();
                temp.setRelease(source.isRelease());

                proxyMissionItem = temp;
                break;
            }

            case SET_SERVO: {
                org.droidplanner.core.mission.commands.SetServo source = (org.droidplanner.core.mission.commands.SetServo) item;

                SetServo temp = new SetServo();
                temp.setChannel(source.getChannel());
                temp.setPwm(source.getPwm());

                proxyMissionItem = temp;
                break;
            }
            case CONDITION_YAW: {
                ConditionYaw source = (ConditionYaw) item;

                YawCondition temp = new YawCondition();
                temp.setAngle(source.getAngle());
                temp.setAngularSpeed(source.getAngularSpeed());
                temp.setRelative(source.isRelative());

                proxyMissionItem = temp;
                break;
            }

            default:
                proxyMissionItem = null;
                break;
        }

        return proxyMissionItem;
    }

    public static MissionItemMessage getRawMissionItem(msg_mission_item missionItem) {
        if (missionItem == null)
            return null;

        MissionItemMessage rawMissionItem = new MissionItemMessage();
        rawMissionItem.setAutocontinue(missionItem.autocontinue);
        rawMissionItem.setCommand(missionItem.command);
        rawMissionItem.setCompId(missionItem.compid);
        rawMissionItem.setCurrent(missionItem.current);
        rawMissionItem.setFrame(missionItem.frame);
        rawMissionItem.setParam1(missionItem.param1);
        rawMissionItem.setParam2(missionItem.param2);
        rawMissionItem.setParam3(missionItem.param3);
        rawMissionItem.setParam4(missionItem.param4);
        rawMissionItem.setSeq(missionItem.seq);
        rawMissionItem.setSysId(missionItem.sysid);
        rawMissionItem.setTarget_component(missionItem.target_component);
        rawMissionItem.setTarget_system(missionItem.target_system);
        rawMissionItem.setX(missionItem.x);
        rawMissionItem.setY(missionItem.y);
        rawMissionItem.setZ(missionItem.z);

        return rawMissionItem;
    }

    public static msg_mission_item getMsgMissionItem(MissionItemMessage mim) {
        if (mim == null)
            return null;

        msg_mission_item item = new msg_mission_item();
        item.autocontinue = mim.getAutocontinue();
        item.command = (short) mim.getCommand();
        item.compid = mim.getCompId();
        item.current = mim.getCurrent();
        item.frame = mim.getFrame();
        item.param1 = mim.getParam1();
        item.param2 = mim.getParam2();
        item.param3 = mim.getParam3();
        item.param4 = mim.getParam4();
        item.seq = (short) mim.getSeq();
        item.sysid = mim.getSysId();
        item.target_component = mim.getTarget_component();
        item.target_system = mim.getTarget_system();
        item.x = mim.getX();
        item.y = mim.getY();
        item.z = mim.getZ();

        return item;
    }
}
