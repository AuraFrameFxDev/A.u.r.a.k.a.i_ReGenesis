# Navigation Map

This document outlines the complete navigation flow of the AuraKai application, from the main gate system to the individual screens.

## Gate System (Main Navigation Hub)

The main navigation hub of the application is the Gate System, which is composed of a series of gate cards that provide access to the different sections of the application.

*   **Oracle Drive Gate:** Contains all Genesis-specific screens, including the `GenesisNavigation` nested NavHost.
*   **Agent Hub Gate:** Contains all agent-related screens, including agent management, chat, loadout, and progression screens.
*   **ROM Tools Gate:** Contains all ROM-related tools.
*   **Root Tools Gate:** Contains all root-related tools.
*   **ChromaCore Gate:** Contains all UI/UX and theming-related screens.
*   **Code Assist Gate:** Contains all code-related assistance features.
*   **Help Desk Gate:** Contains all help and support-related screens.
*   **Sentinels Fortress Gate:** Contains all security-related screens.
*   **Sphere Grid Gate:** Contains the agent progression and advancement screens.
*   **Terminal Gate:** Contains a terminal emulator.
*   **UI/UX Design Studio Gate:** Contains all UI/UX design-related tools.

## Nested Navigation

The following nested NavHosts are used within the Gate System:

*   **GenesisNavigation:** Nested within the Oracle Drive Gate, this NavHost contains all Genesis-specific screens.
*   **AuraFrameNavigation:** This NavHost is not part of the Gate System, but is used to navigate between the main screens of the application. It is not clear from the documentation where this NavHost should be integrated, but I will assume that it should be integrated into the `MainScreen` of the application.

## Screen Mapping

The following table maps the screens to their corresponding gates:

| Gate                      | Screen                                      |
| ------------------------- | ------------------------------------------- |
| Oracle Drive              | AGENT_NEXUS                                 |
| Oracle Drive              | CONSCIOUSNESS_VISUALIZER                    |
| Oracle Drive              | FUSION_MODE                                 |
| Oracle Drive              | CONFERENCE_ROOM                             |
| Agent Hub                 | Agent Management                            |
| Agent Hub                 | Agent Chat                                  |
| Agent Hub                 | Agent Loadout                               |
| Agent Hub                 | Agent Progression                           |
| ROM Tools                 | ROM Tools                                   |
| Root Tools                | Root Tools                                  |
| ChromaCore                | Theme Engine                                |
| Code Assist               | Code Assist                                 |
| Help Desk                 | Help Desk                                   |
| Sentinels Fortress        | Security                                    |
| Sphere Grid               | Agent Progression                           |
| Terminal                  | Terminal                                    |
| UI/UX Design Studio       | UI/UX Design Studio                         |
