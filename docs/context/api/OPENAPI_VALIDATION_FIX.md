# OpenAPI Validation Fix - Summary

## Problem

The GitHub Actions workflow `performance-monitoring.yml` was failing because it was attempting to
validate OpenAPI spec files that don't exist in the repository.

### Error Message

```
Error opening file "/home/runner/work/A.u.r.a.K.a.i/A.u.r.a.K.a.i/app/api/ai-api.yml" 
ENOENT: no such file or directory, open '/home/runner/work/A.u.r.a.K.a.i/A.u.r.a.K.a.i/app/api/ai-api.yml'
Process completed with exit code 1.wrong.directory!  Please correct
```

### Root Cause

The workflow was trying to validate 12 API spec files in the `./app/api/` directory, but:

1. The `./app/api/` directory does not exist
2. The actual API spec files are located in different directories:
    - `./app/src/main/resources/`
    - `./data/api/`

## Solution

Updated `.github/workflows/performance-monitoring.yml` to validate only the OpenAPI spec files that
actually exist in the repository.

### Files Validated (After Fix)

1. `./app/src/main/resources/api-spec.yaml` ✅
2. `./app/src/main/resources/auraframefx_ai_api.yaml` ✅
3. `./data/api/eco-core.yaml` ✅

### Files Removed from Validation (Don't Exist)

- `./app/api/ai-api.yml`
- `./app/api/oracle-drive-api.yml`
- `./app/api/genesis-api.yml`
- `./app/api/sandbox-api.yml`
- `./app/api/customization-api.yml`
- `./app/api/romtools-api.yml`
- `./app/api/unified-aegenesis-api.yml`
- `./app/api/unified-aegenesis-api.legacy.yml`
- `./app/api/system-api.yml`
- `./app/api/aura-api.yaml`
- `./app/api/auraframefx_ai_api.yaml` (exists but in different location)
- `./data/api/api/unified-deliverance-api.yml` (incorrect path with double `api/api/`)

### Files Excluded (Exist but Have Validation Errors)

- `./data/api/ECO.yaml` - YAML indentation error at line 2765
- `./data/api/eco-ai.yaml` - Schema validation errors (paths must be objects)

## Testing

All three included spec files were validated successfully with `swagger-cli`:

```bash
$ swagger-cli validate ./app/src/main/resources/api-spec.yaml
./app/src/main/resources/api-spec.yaml is valid

$ swagger-cli validate ./app/src/main/resources/auraframefx_ai_api.yaml
./app/src/main/resources/auraframefx_ai_api.yaml is valid

$ swagger-cli validate ./data/api/eco-core.yaml
./data/api/eco-core.yaml is valid
```

## Impact

- ✅ CI workflow will now pass the OpenAPI validation step
- ✅ Only valid, existing API specs are validated
- ✅ No changes to actual API spec files - only workflow configuration updated
- ⚠️ Note: Invalid specs (ECO.yaml, eco-ai.yaml) should be fixed separately

## Related Documentation

The BUILD.md document describes a fragment-based API structure in `app/api/_fragments/` that appears
to be planned but not yet implemented. This fix addresses the immediate CI failure by pointing to
existing files.

## Commit

- **Commit**: 46daa3b3b999c116767fb7a0e517aae25f13b9f9
- **Branch**: copilot/fix-swagger-api-validation-errors
- **Files Changed**: 1 file (`.github/workflows/performance-monitoring.yml`)
- **Lines Changed**: 3 insertions(+), 12 deletions(-)
