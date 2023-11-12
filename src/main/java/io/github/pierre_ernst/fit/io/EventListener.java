package io.github.pierre_ernst.fit.io;

import com.garmin.fit.*;

import io.github.pierre_ernst.fit.model.FitData;

public class EventListener implements FileIdMesgListener, FileCreatorMesgListener, TimestampCorrelationMesgListener,
		SoftwareMesgListener, SlaveDeviceMesgListener, CapabilitiesMesgListener, FileCapabilitiesMesgListener,
		MesgCapabilitiesMesgListener, FieldCapabilitiesMesgListener, DeviceSettingsMesgListener,
		UserProfileMesgListener, HrmProfileMesgListener, SdmProfileMesgListener, BikeProfileMesgListener,
		ConnectivityMesgListener, WatchfaceSettingsMesgListener, OhrSettingsMesgListener, TimeInZoneMesgListener,
		ZonesTargetMesgListener, SportMesgListener, HrZoneMesgListener, SpeedZoneMesgListener, CadenceZoneMesgListener,
		PowerZoneMesgListener, MetZoneMesgListener, DiveSettingsMesgListener, DiveAlarmMesgListener,
		DiveApneaAlarmMesgListener, DiveGasMesgListener, GoalMesgListener, ActivityMesgListener, SessionMesgListener,
		LapMesgListener, LengthMesgListener, RecordMesgListener, EventMesgListener, DeviceInfoMesgListener,
		DeviceAuxBatteryInfoMesgListener, TrainingFileMesgListener, WeatherConditionsMesgListener,
		WeatherAlertMesgListener, GpsMetadataMesgListener, CameraEventMesgListener, GyroscopeDataMesgListener,
		AccelerometerDataMesgListener, MagnetometerDataMesgListener, BarometerDataMesgListener,
		ThreeDSensorCalibrationMesgListener, OneDSensorCalibrationMesgListener, VideoFrameMesgListener,
		ObdiiDataMesgListener, NmeaSentenceMesgListener, AviationAttitudeMesgListener, VideoMesgListener,
		VideoTitleMesgListener, VideoDescriptionMesgListener, VideoClipMesgListener, SetMesgListener, JumpMesgListener,
		SplitMesgListener, ClimbProMesgListener, FieldDescriptionMesgListener, DeveloperDataIdMesgListener,
		CourseMesgListener, CoursePointMesgListener, SegmentIdMesgListener, SegmentLeaderboardEntryMesgListener,
		SegmentPointMesgListener, SegmentLapMesgListener, SegmentFileMesgListener, WorkoutMesgListener,
		WorkoutSessionMesgListener, WorkoutStepMesgListener, ExerciseTitleMesgListener, ScheduleMesgListener,
		TotalsMesgListener, WeightScaleMesgListener, BloodPressureMesgListener, MonitoringInfoMesgListener,
		MonitoringMesgListener, HrMesgListener, StressLevelMesgListener, MemoGlobMesgListener, AntChannelIdMesgListener,
		AntRxMesgListener, AntTxMesgListener, ExdScreenConfigurationMesgListener, ExdDataFieldConfigurationMesgListener,
		ExdDataConceptConfigurationMesgListener, DiveSummaryMesgListener, HrvMesgListener, TankUpdateMesgListener,
		TankSummaryMesgListener, PadMesgListener {

	private FitData fitData = new FitData();

	public FitData getFitData() {
		return fitData;
	}

	@Override
	public void onMesg(FileIdMesg fileIdMsg) {
		fitData.add(fileIdMsg);
	}

	@Override
	public void onMesg(WorkoutMesg workoutMsg) {
		fitData.add(workoutMsg);
	}

	@Override
	public void onMesg(WorkoutStepMesg workoutStepMsg) {
		fitData.add(workoutStepMsg);
	}

	@Override
	public void onMesg(PadMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(TankSummaryMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(TankUpdateMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(HrvMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DiveSummaryMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ExdDataConceptConfigurationMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ExdDataFieldConfigurationMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ExdScreenConfigurationMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(AntTxMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(AntRxMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(AntChannelIdMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(MemoGlobMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(StressLevelMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(HrMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(MonitoringMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(MonitoringInfoMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(BloodPressureMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(WeightScaleMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(TotalsMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ScheduleMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ExerciseTitleMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(WorkoutSessionMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SegmentFileMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SegmentLapMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SegmentPointMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SegmentLeaderboardEntryMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SegmentIdMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(CoursePointMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(CourseMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DeveloperDataIdMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(FieldDescriptionMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ClimbProMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SplitMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(JumpMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SetMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(VideoClipMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(VideoDescriptionMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(VideoTitleMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(VideoMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(AviationAttitudeMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(NmeaSentenceMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ObdiiDataMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(VideoFrameMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(OneDSensorCalibrationMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ThreeDSensorCalibrationMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(BarometerDataMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(MagnetometerDataMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(AccelerometerDataMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(GyroscopeDataMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(CameraEventMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(GpsMetadataMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(WeatherAlertMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(WeatherConditionsMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(TrainingFileMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DeviceAuxBatteryInfoMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DeviceInfoMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(EventMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(RecordMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(LengthMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(LapMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SessionMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ActivityMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(GoalMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DiveGasMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DiveApneaAlarmMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DiveAlarmMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DiveSettingsMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(MetZoneMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(PowerZoneMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(CadenceZoneMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SpeedZoneMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(HrZoneMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SportMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ZonesTargetMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(TimeInZoneMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(OhrSettingsMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(WatchfaceSettingsMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(ConnectivityMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(BikeProfileMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SdmProfileMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(HrmProfileMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(UserProfileMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(DeviceSettingsMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(FieldCapabilitiesMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(MesgCapabilitiesMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(FileCapabilitiesMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(CapabilitiesMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SlaveDeviceMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(SoftwareMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(TimestampCorrelationMesg arg0) {
		fitData.add(arg0);
	}

	@Override
	public void onMesg(FileCreatorMesg arg0) {
		fitData.add(arg0);
	}
}
