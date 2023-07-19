variable "aws_region" {
  type = string
  default = "eu-central-1"
}

variable "app_name" {
  type = string
  default = "personal-metadata"
}

variable "aws_key_id" {
  type = string
  sensitive = true
}

variable "aws_secret_key" {
  type = string
  sensitive = true
}

variable "aws_assume_role_external_id" {
  type = string
  sensitive = true
}

variable "aws_terraform_role_arn" {
  type = string
  default = "arn:aws:iam::844933496707:role/TerraformRole"
}

# this Cognito user pool is defined elsewhere
variable "personal_cognito_user_pool_id" {
  type = string
  default = "eu-central-1_S5NuJmFiM"
}