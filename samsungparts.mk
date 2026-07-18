SEC_PARTS_PATH := packages/apps/SamsungParts

# Soong
PRODUCT_SOONG_NAMESPACES += $(SEC_PARTS_PATH)

# SEPolicy
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += $(SEC_PARTS_PATH)/sepolicy/private
SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += $(SEC_PARTS_PATH)/sepolicy/public

# Samsung Parts
PRODUCT_PACKAGES += \
    SamsungParts \
    init.sec-battery.service.rc \
    privapp-permissions-samsungparts.xml
