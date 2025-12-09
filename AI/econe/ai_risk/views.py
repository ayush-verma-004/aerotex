import logging
from rest_framework.decorators import api_view
from rest_framework.response import Response
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from .ml_model import predict_risk
from .serializers import RiskScoreRequestSerializer

logger = logging.getLogger(__name__)

@swagger_auto_schema(
    method='post',
    request_body=RiskScoreRequestSerializer,
    responses={
        200: openapi.Response("Risk Score Result", schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'riskScore': openapi.Schema(type=openapi.TYPE_NUMBER),
                'riskLevel': openapi.Schema(type=openapi.TYPE_STRING),
                'modelVersion': openapi.Schema(type=openapi.TYPE_STRING),
            }
        )),
        400: "Invalid JSON or Data",
        500: "Internal Server Error"
    }
)
@api_view(['POST'])
def risk_score_view(request):
    """
    Calculate risk score based on carbon project, land, and farmer details.
    """
    try:
        serializer = RiskScoreRequestSerializer(data=request.data)
        if serializer.is_valid():
            # serializer.validated_data contains cleaned data with defaults
            # However, predict_risk expects the nested structure which validated_data preserves
            # gracefully. 
            
            # Note: serializer.data vs serializer.validated_data
            # validated_data returns objects/dicts with defaults applied IF declared.
            # But we need to ensure the structure matches what build_feature_row expects.
            # The serializer structure matches the payload structure perfectly.
            
            payload = serializer.validated_data
            # Need to handle potential None/Empty defaults if not fully populated by serializer defaults
            # (Our serializer uses defaults so it should be fine).
            
            result = predict_risk(payload)
            return Response(result)
        else:
            return Response(serializer.errors, status=400)

    except Exception as e:
        logger.error(f"Error in risk_score_view: {e}", exc_info=True)
        return Response({"error": "Internal Server Error", "details": str(e)}, status=500)
