import joblib
import os
import pandas as pd
import logging

logger = logging.getLogger(__name__)

# Load once at import time
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "risk_model.joblib")

model = None
try:
    if os.path.exists(MODEL_PATH):
        model = joblib.load(MODEL_PATH)
    else:
        logger.warning(f"Model file not found at {MODEL_PATH}. Risk scoring will be disabled or mocked.")
except Exception as e:
    logger.error(f"Failed to load model from {MODEL_PATH}: {e}")

FEATURE_COLUMNS = [
    "carbonAmount",
    "validityYears",
    "landArea",
    "latitude",
    "longitude",
    "areaDiff",
    "cropTypesCount",
    "hasIrrigation",
    "soilType",
    "farmerState",
]


def _safe_float(val, default=0.0):
    try:
        if val is None:
            return default
        return float(val)
    except (ValueError, TypeError):
        return default


def _safe_int(val, default=0):
    try:
        if val is None:
            return default
        return int(val)
    except (ValueError, TypeError):
        return default


def build_feature_row(payload: dict) -> pd.DataFrame:
    """
    payload structure:
    {
      "carbonProject": { ... },
      "land": { ... },
      "farmer": { ... }
    }
    """
    if not isinstance(payload, dict):
        logger.error(f"Invalid payload type: {type(payload)}")
        payload = {}

    carbon = payload.get("carbonProject")
    if not isinstance(carbon, dict):
        carbon = {}

    land = payload.get("land")
    if not isinstance(land, dict):
        land = {}

    farmer = payload.get("farmer")
    if not isinstance(farmer, dict):
        farmer = {}
    
    # Safely get nested dictionaries
    farm_details = farmer.get("farmDetails")
    if not isinstance(farm_details, dict):
        farm_details = {}
        
    farmer_address = farmer.get("address")
    if not isinstance(farmer_address, dict):
        farmer_address = {}

    land_area_val = _safe_float(land.get("landArea"))
    farmer_land_area = _safe_float(farm_details.get("landAreaInAcres"))
    
    area_diff = abs(land_area_val - farmer_land_area)

    crop_types = farmer.get("cropTypes")
    if not isinstance(crop_types, list):
        crop_types = []
    crop_types_count = len(crop_types)

    has_irrigation = 1 if farm_details.get("irrigationAvailable") else 0

    row = {
        "carbonAmount": _safe_float(carbon.get("carbonAmount")),
        "validityYears": _safe_int(carbon.get("validityYears")),
        "landArea": land_area_val,
        "latitude": _safe_float(land.get("latitude")),
        "longitude": _safe_float(land.get("longitude")),
        "areaDiff": area_diff,
        "cropTypesCount": crop_types_count,
        "hasIrrigation": has_irrigation,
        "soilType": land.get("soilType", "UNKNOWN"),
        "farmerState": farmer_address.get("state", "UNKNOWN"),
    }

    # Ensure all required keys for DataFrame are present (though we just built them above)
    # DataFrame with one row to feed into sklearn pipeline
    return pd.DataFrame([row], columns=FEATURE_COLUMNS)


def predict_risk(payload: dict) -> dict:
    if model is None:
        return {
            "riskScore": -1.0,
            "riskLevel": "UNKNOWN",
            "modelVersion": "v1",
            "error": "Model not loaded"
        }

    try:
        df = build_feature_row(payload)
        
        # model is sklearn Pipeline with predict_proba
        # Check if model has predict_proba
        if not hasattr(model, "predict_proba"):
             logger.error("Loaded object does not have predict_proba method")
             return {
                 "riskScore": -1.0, 
                 "riskLevel": "ERROR", 
                 "modelVersion": "v1"
             }

        proba = model.predict_proba(df)[0]  # [prob_not_suspicious, prob_suspicious]
        risk_score = float(proba[1])

        if risk_score < 0.3:
            level = "LOW"
        elif risk_score < 0.6:
            level = "MEDIUM"
        else:
            level = "HIGH"

        return {
            "riskScore": round(risk_score, 3),
            "riskLevel": level,
            "modelVersion": "v1",
        }
    except Exception as e:
        logger.error(f"Error during risk prediction: {e}", exc_info=True)
        return {
            "riskScore": -1.0,
            "riskLevel": "ERROR",
            "modelVersion": "v1",
            "error": str(e)
        }
