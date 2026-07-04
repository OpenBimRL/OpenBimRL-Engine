#!/usr/bin/env bash
# Creates or updates GitHub issues for RFC markdown files under rfc/.
# Each RFC is tracked by labels: rfc + rfc-<number>.

set -euo pipefail

RFC_DIR="${RFC_DIR:-rfc}"
SYNC_ALL="${SYNC_ALL:-false}"
BEFORE_SHA="${BEFORE_SHA:-}"
CURRENT_SHA="${CURRENT_SHA:-HEAD}"
REPO_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}"

if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI is required"
  exit 1
fi

ensure_label() {
  local name="$1"
  local description="$2"
  gh label create "$name" --description "$description" --color "5319E7" --force >/dev/null 2>&1 || true
}

find_existing_issue() {
  local label="$1"
  gh issue list --label "$label" --state all --limit 1 | awk 'NR==1 { print $1; exit }'
}

create_issue() {
  local title="$1"
  local body_file="$2"
  local label="$3"
  local issue_url issue_num

  issue_url="$(gh issue create --title "$title" --body-file "$body_file" --label "rfc" --label "$label")"
  issue_num="${issue_url##*/}"
  if [[ ! "$issue_num" =~ ^[0-9]+$ ]]; then
    echo "Failed to parse issue number from: $issue_url" >&2
    exit 1
  fi
  echo "$issue_num"
}

rfc_number_from_path() {
  local path="$1"
  local base
  base="$(basename "$path" .md)"
  if [[ "$base" =~ ^rfc-([0-9]+) ]]; then
    echo "${BASH_REMATCH[1]}"
  fi
}

issue_title_from_file() {
  local file="$1"
  local num="$2"
  local heading
  heading="$(grep -m1 '^# ' "$file" | sed 's/^# //' || true)"
  if [[ -z "$heading" ]]; then
    echo "[RFC-${num}] $(basename "$file" .md)"
    return
  fi
  if [[ "$heading" =~ ^RFC-${num}[[:space:]:-]+(.+)$ ]]; then
    echo "[RFC-${num}] ${BASH_REMATCH[1]}"
  else
    echo "[RFC-${num}] ${heading}"
  fi
}

build_issue_body() {
  local file="$1"
  local blob_url="${REPO_URL}/blob/${CURRENT_SHA}/${file}"
  cat "$file"
  printf '\n\n---\n\n'
  printf '_Synced from [`%s`](%s) (%s)._\n' "$file" "$blob_url" "$(date -u +"%Y-%m-%d %H:%M UTC")"
}

collect_changed_rfc_files() {
  if [[ "$SYNC_ALL" == "true" ]]; then
    find "$RFC_DIR" -maxdepth 1 -type f -name 'rfc-*.md' | sort
    return
  fi

  if [[ -z "$BEFORE_SHA" || "$BEFORE_SHA" == "0000000000000000000000000000000000000000" ]]; then
    find "$RFC_DIR" -maxdepth 1 -type f -name 'rfc-*.md' | sort
    return
  fi

  git diff --name-only "$BEFORE_SHA" "$CURRENT_SHA" -- "$RFC_DIR"/rfc-*.md | sort
}

sync_rfc_file() {
  local file="$1"
  local num title body label issue_num body_file

  num="$(rfc_number_from_path "$file")"
  if [[ -z "$num" ]]; then
    echo "Skipping $file (not an rfc-<n>*.md file)"
    return
  fi

  title="$(issue_title_from_file "$file" "$num")"
  label="rfc-${num}"
  body_file="$(mktemp)"
  build_issue_body "$file" > "$body_file"

  ensure_label "rfc" "RFC tracking issue"
  ensure_label "$label" "RFC ${num}"

  issue_num="$(find_existing_issue "$label")"

  if [[ -n "$issue_num" ]]; then
    gh issue edit "$issue_num" --title "$title" --body-file "$body_file" >/dev/null
    echo "Updated issue #${issue_num} for ${file}"
  else
    issue_num="$(create_issue "$title" "$body_file" "$label")"
    echo "Created issue #${issue_num} for ${file}"
  fi

  rm -f "$body_file"
}

mapfile -t RFC_FILES < <(collect_changed_rfc_files)

if [[ ${#RFC_FILES[@]} -eq 0 ]]; then
  echo "No RFC files to sync."
  exit 0
fi

echo "Syncing ${#RFC_FILES[@]} RFC file(s)..."
for file in "${RFC_FILES[@]}"; do
  [[ -f "$file" ]] || continue
  sync_rfc_file "$file"
done
