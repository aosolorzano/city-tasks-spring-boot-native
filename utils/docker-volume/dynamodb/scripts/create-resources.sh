#!/bin/bash

echo "CREATING DEVICES TABLE..."
awslocal dynamodb create-table                                \
  --table-name Devices                                        \
  --attribute-definitions AttributeName=id,AttributeType=S    \
  --key-schema AttributeName=id,KeyType=HASH                  \
  --billing-mode PAY_PER_REQUEST
echo "DONE!"

echo ""
echo "PUTTING DEVICE ITEM..."
awslocal dynamodb put-item                                    \
    --table-name Devices                                      \
    --item file:///var/lib/localstack/devices.json
echo "DONE!"
