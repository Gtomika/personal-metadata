# Personal Metadata storage

Java app that stores metadata about personal files
(mostly images) in AWS DynamoDB

# Access

Access is granted with my personal AWS Cognito user pool. It is 
not public, only I can create new users. However, you may 
attach this app to another user pool in your own AWS account, by 
replacing the `personal_cognito_user_pool_id` variable in 
`terraform/variables.tf` with your own pools ID.

# DevOps

Terraform IaC is used to provision AWS resources. All jobs 
run on GitHub Actions CI/CD pipeline.