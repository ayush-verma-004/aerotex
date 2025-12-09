from rest_framework import serializers

class CarbonProjectSerializer(serializers.Serializer):
    carbonAmount = serializers.FloatField(default=0.0)
    validityYears = serializers.IntegerField(default=0)

class LandSerializer(serializers.Serializer):
    landArea = serializers.FloatField(default=0.0)
    latitude = serializers.FloatField(default=0.0)
    longitude = serializers.FloatField(default=0.0)
    soilType = serializers.CharField(default="UNKNOWN", required=False)

class FarmDetailsSerializer(serializers.Serializer):
    landAreaInAcres = serializers.FloatField(default=0.0)
    irrigationAvailable = serializers.BooleanField(default=False)

class FarmerAddressSerializer(serializers.Serializer):
    state = serializers.CharField(default="UNKNOWN", required=False)

class FarmerSerializer(serializers.Serializer):
    farmDetails = FarmDetailsSerializer(required=False)
    address = FarmerAddressSerializer(required=False)
    cropTypes = serializers.ListField(
        child=serializers.CharField(), required=False, default=list
    )

class RiskScoreRequestSerializer(serializers.Serializer):
    carbonProject = CarbonProjectSerializer(required=False)
    land = LandSerializer(required=False)
    farmer = FarmerSerializer(required=False)
