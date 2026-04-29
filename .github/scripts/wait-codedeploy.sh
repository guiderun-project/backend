#!/usr/bin/env bash
set -euo pipefail

deployment_id="${1:?deployment id is required}"
poll_interval="${CODEDEPLOY_POLL_INTERVAL_SECONDS:-15}"
max_attempts="${CODEDEPLOY_MAX_ATTEMPTS:-120}"

write_summary() {
  local status="$1"
  local details="${2:-}"

  if [[ -z "${GITHUB_STEP_SUMMARY:-}" ]]; then
    return 0
  fi

  {
    echo "### CodeDeploy deployment"
    echo "- Deployment ID: \`$deployment_id\`"
    echo "- Status: \`$status\`"

    if [[ -n "$details" ]]; then
      echo
      echo '```json'
      echo "$details"
      echo '```'
    fi
  } >> "$GITHUB_STEP_SUMMARY"
}

deployment_details() {
  aws deploy get-deployment \
    --deployment-id "$deployment_id" \
    --query 'deploymentInfo.{status:status,errorInformation:errorInformation,deploymentOverview:deploymentOverview}' \
    --output json
}

echo "Waiting for CodeDeploy deployment $deployment_id..."

for ((attempt = 1; attempt <= max_attempts; attempt++)); do
  status="$(aws deploy get-deployment \
    --deployment-id "$deployment_id" \
    --query 'deploymentInfo.status' \
    --output text)"

  echo "[$attempt/$max_attempts] CodeDeploy status: $status"

  case "$status" in
    Succeeded)
      write_summary "$status"
      echo "CodeDeploy deployment $deployment_id succeeded."
      exit 0
      ;;
    Failed|Stopped)
      details="$(deployment_details)"
      echo "CodeDeploy deployment $deployment_id ended with status $status."
      echo "$details"
      write_summary "$status" "$details"
      exit 1
      ;;
  esac

  if (( attempt < max_attempts )); then
    sleep "$poll_interval"
  fi
done

details="$(deployment_details || true)"
echo "Timed out waiting for CodeDeploy deployment $deployment_id after $max_attempts checks."
echo "$details"
write_summary "Timed out" "$details"
exit 1
