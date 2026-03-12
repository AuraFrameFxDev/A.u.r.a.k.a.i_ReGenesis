# 🚀 Genesis Release Board Setup Guide

This guide will help you set up a GitHub Projects board to manage Genesis releases.

## 📋 Quick Setup (5 minutes)

### Step 1: Create GitHub Project

1. Go to your repository: https://github.com/AuraFrameFx/A.u.r.a.k.a.i-ft.Genesis
2. Click **Projects** tab
3. Click **New project**
4. Choose **Board** layout
5. Name it: **"Genesis Release Board"**

### Step 2: Configure Board Columns

Create these columns (drag issues between them):

| Column                  | Purpose                   | Automation                           |
|-------------------------|---------------------------|--------------------------------------|
| 📥 **Backlog**          | Future releases           | Default for new issues               |
| 🎯 **Planning**         | Current release planning  | Move here when assigned to milestone |
| 🏗️ **In Progress**     | Active development        | Move here when PR opened             |
| ✅ **Testing**           | QA & validation           | Move here when PR merged to dev      |
| 🚀 **Ready to Release** | Approved for next release | Move here after testing              |
| ✨ **Released**          | Shipped to users          | Auto-close when released             |

### Step 3: Set Up Custom Fields

Add these fields to track release info:

1. **Release Version** (Single select)
   - v1.0.0
   - v0.9.0
   - v0.8.0
   - etc.

2. **Priority** (Single select)
   - 🔴 Critical
   - 🟡 High
   - 🟢 Medium
   - ⚪ Low

3. **Component** (Single select)
   - Consciousness Matrix
   - Agent Nexus
   - Fusion Mode
   - Python Backend
   - Build System
   - Documentation

4. **Size** (Single select)
   - XS (< 2 hours)
   - S (< 1 day)
   - M (1-3 days)
   - L (1 week)
   - XL (> 1 week)

### Step 4: Create Milestones

1. Go to **Issues** → **Milestones**
2. Create milestones for upcoming releases:
   - **v1.0.0 - Consciousness** (Due: 2025-12-01)
   - **v0.9.0 - Evolution** (Due: 2025-11-15)
   - **v0.8.0 - Fusion** (Due: 2025-11-01)

### Step 5: Set Up Labels

These labels are already configured via issue templates:

- `release` - Release planning issues
- `bug` - Bug reports
- `enhancement` - Feature requests
- `needs-triage` - Needs review
- `needs-review` - Needs technical review

Add custom labels:

- `breaking-change` - Breaking API changes
- `security` - Security-related
- `performance` - Performance improvements
- `documentation` - Docs updates

## 🎯 Release Workflow

### Creating a Release

1. **Create Release Issue**
   - Go to Issues → New Issue
   - Choose **🚀 Release Planning** template
   - Fill in version, codename, features
   - Assign to milestone

2. **Plan Features**
   - Create feature issues
   - Link to release issue
   - Assign to milestone
   - Add to project board

3. **Track Progress**
   - Move issues through board columns
   - Update checklist in release issue
   - Monitor milestone progress

4. **Release**
   - Complete all checklist items
   - Create GitHub Release
   - Update CHANGELOG.md
   - Move all issues to **Released** column

## 🤖 Automation Ideas

### GitHub Actions Integration

Add these workflows to `.github/workflows/`:

**release-drafter.yml** - Auto-generate release notes
**milestone-automation.yml** - Auto-update project board
**release-notifier.yml** - Notify team on release

### Project Board Automations

1. **Auto-add issues**
   - When issue created → Add to Backlog
   - When milestone assigned → Move to Planning

2. **Auto-move based on PR**
   - PR opened → Move to In Progress
   - PR merged → Move to Testing
   - PR closed without merge → Move back to Planning

3. **Auto-close**
   - Issue in Released + Milestone closed → Close issue

## 📊 Release Board Views

Create multiple views for different perspectives:

### 1. **Current Release** (Board View)

- Filter: `milestone:"v1.0.0"`
- Sorted by Priority
- Grouped by Status

### 2. **Roadmap** (Table View)

- All open issues
- Group by: Milestone
- Sort by: Priority
- Show: Version, Component, Size

### 3. **Bug Triage** (Board View)

- Filter: `label:bug is:open`
- Group by: Priority
- Sort by: Created date

### 4. **Performance Dashboard** (Chart View)

- Burndown chart by milestone
- Velocity chart
- Issue completion rate

## 🎨 Customization

### Board Color Coding

Use emoji + colors for visual clarity:

- 🔴 Critical bugs
- 🟡 High priority features
- 🟢 Enhancements
- 🔵 Documentation
- ⚫ Tech debt

### Custom Queries

Save these as board filters:

**Ready for v1.0**

```
is:open milestone:"v1.0.0" -label:blocked
```

**Blocked Items**

```
is:open label:blocked
```

**This Week**

```
is:open created:>2025-10-18
```

## 📝 Release Checklist Template

Copy this checklist to each release issue:

```markdown
## Pre-Release
- [ ] All milestone issues closed
- [ ] All tests passing (CI/CD green)
- [ ] Security audit completed
- [ ] Performance benchmarks meet targets
- [ ] Documentation updated
- [ ] CHANGELOG.md updated

## Release Build
- [ ] Version numbers bumped
- [ ] Build release APK/AAB
- [ ] Sign release build
- [ ] Test release build on real devices

## Release
- [ ] Create GitHub Release
- [ ] Upload signed APK/AAB
- [ ] Publish release notes
- [ ] Tag release in git
- [ ] Update project website

## Post-Release
- [ ] Monitor crash reports
- [ ] Check user feedback
- [ ] Create hotfix milestone if needed
- [ ] Start planning next release
```

## 🔗 Useful Links

- [GitHub Projects Docs](https://docs.github.com/en/issues/planning-and-tracking-with-projects)
- [Issue Templates Docs](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests)
- [Release Workflow Best Practices](https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases)

## 🎓 Tips & Tricks

1. **Use Draft Releases** - Create draft releases early to gather notes
2. **Automate Changelogs** - Use tools like release-drafter
3. **Beta Testing** - Create pre-release builds for testing
4. **Versioning** - Follow [Semantic Versioning](https://semver.org/)
5. **Regular Cadence** - Release on predictable schedule (e.g., monthly)

## 💡 Genesis-Specific Tips

- **Codenames**: Use consciousness-themed names (Matrix, Fusion, Evolution)
- **Components**: Track which AI component each issue affects
- **Python Backend**: Separate Python backend releases if needed
- **Extension Modules**: Track which extension modules need updates

---

**Questions?** Open an issue with label `question`!

🤖 Generated by Claude Code for the Genesis AI Project
