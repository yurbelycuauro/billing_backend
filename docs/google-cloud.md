# Google Cloud Pub/Sub and Storage

The project uses the official Spring Cloud GCP starters for Pub/Sub and Cloud
Storage. They use Application Default Credentials (ADC), so credentials are
not stored in the repository or configured in `application.properties`.

## Local development

Install the Google Cloud CLI and authenticate the local application:

```shell
gcloud auth application-default login
```

Set `GOOGLE_CLOUD_PROJECT` if the project ID cannot be detected automatically.

## Deployment on Google Cloud

Attach a service account to the runtime and grant it only the IAM permissions
needed for Pub/Sub and Cloud Storage. ADC obtains its identity from the runtime
metadata server; do not create or distribute service account key files.

See the [Application Default Credentials documentation](https://cloud.google.com/docs/authentication/application-default-credentials)
and the [Spring Cloud GCP reference](https://googlecloudplatform.github.io/spring-cloud-gcp/reference/html/index.html).
