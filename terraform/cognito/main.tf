resource "aws_cognito_user_pool_client" "web_client" {
  name         = "${var.resource_identification}-java-client"
  user_pool_id = var.personal_user_pool_id

  prevent_user_existence_errors = "ENABLED"
  generate_secret = false
  refresh_token_validity = 90 # days
  access_token_validity = 12 # hours, how long the temporary AWS credentials are valid
  id_token_validity = 12

  # these actions will be enabled in the client
  explicit_auth_flows = [
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_ADMIN_USER_PASSWORD_AUTH"
  ]

  supported_identity_providers = ["COGNITO"]
}

locals {
  cognito_user_pool_provider_name = "cognito-idp.${var.aws_region}.amazonaws.com/${var.personal_user_pool_id}"
}

resource "aws_cognito_identity_pool" "personal_archive_identity_pool" {
  identity_pool_name = "${var.resource_identification}-identity-pool"
  allow_unauthenticated_identities = false
  allow_classic_flow               = false

  # allow users from the user pool to request credentials from this identity pool
  cognito_identity_providers {
    client_id = aws_cognito_user_pool_client.web_client.id
    provider_name = local.cognito_user_pool_provider_name
    server_side_token_check = false
  }
}

data "aws_iam_policy_document" "cognito_user_trust_policy" {
  statement {
    sid = "AllowToBeAssumedByCognitoUser"
    effect = "Allow"
    principals {
      type = "Federated"
      identifiers = ["cognito-identity.amazonaws.com"]
    }
    actions = ["sts:AssumeRoleWithWebIdentity"]
    condition {
      test     = "StringEquals"
      variable = "cognito-identity.amazonaws.com:aud"
      values   = [aws_cognito_identity_pool.personal_archive_identity_pool.id]
    }
    condition {
      test     = "ForAnyValue:StringLike"
      variable = "cognito-identity.amazonaws.com:amr"
      values   = ["authenticated"]
    }
  }
}

# define what a logged in user can do
data "aws_iam_policy_document" "cognito_user_policy" {
  statement {
    sid = "ManageOwnItems"
    effect = "Allow"
    actions = ["dynamodb:*Item"]
    resources = [var.metadata_table_arn]
    condition {
      test     = "ForAllValues:StringEquals"
      variable = "dynamodb:LeadingKeys"
      values   = ["&{cognito-identity.amazonaws.com:sub}"]
    }
  }
  statement {
    sid = "QueryMetadataTable"
    effect = "Allow"
    actions = ["dynamodb:Query"]
    resources = [var.metadata_table_arn]
  }
}

resource "aws_iam_role" "authenticated_user_role" {
  name = "${var.resource_identification}-cognito-user-role"
  assume_role_policy = data.aws_iam_policy_document.cognito_user_trust_policy.json
}

resource "aws_iam_role_policy" "authenticated_user_role_policy" {
  name = "${var.resource_identification}-cognito-user-policy"
  policy = data.aws_iam_policy_document.cognito_user_policy.json
  role   = aws_iam_role.authenticated_user_role.id
}

resource "aws_cognito_identity_pool_roles_attachment" "cognito_user_role_attachment" {
  identity_pool_id = aws_cognito_identity_pool.personal_archive_identity_pool.id
  # all users get this role
  roles = {
    "authenticated" = aws_iam_role.authenticated_user_role.arn
  }
}

# https://docs.aws.amazon.com/cognito/latest/developerguide/iam-roles.html
# https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_examples_dynamodb_items.html