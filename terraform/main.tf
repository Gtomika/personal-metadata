locals {
  resource_identification = "${var.app_name}-${var.aws_region}"
}

resource "aws_dynamodb_table" "metadata_table" {
  name = "${local.resource_identification}-table"
  billing_mode = "PAY_PER_REQUEST"
  deletion_protection_enabled = true

  hash_key = "UserId"
  range_key = "FileId"

  attribute {
    name = "UserId" # Cognito user ID must be the partition key, to make per-user permissions work
    type = "S"
  }

  attribute {
    name = "FileId"
    type = "S"
  }
}

# attach new client and identity pool to personal user pool, with correct permissions
module "cognito_setup" {
  source = "./cognito"
  resource_identification = local.resource_identification
  aws_region = var.aws_region
  personal_user_pool_id = var.personal_cognito_user_pool_id
  metadata_table_arn = aws_dynamodb_table.metadata_table.arn
}