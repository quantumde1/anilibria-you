# Anilibria You

AniLibria client, using API v3 from their project and in Google Material You(a.k.a Material Design 3)

## Base
### Features
- Main screen with 30 last updated titles
- Title screen with description, cover, label
- Searching titles
- Slightly bugged favorites feature
- Title playback using Android's ExoPlayer
- Simple but elegant UI with following Google's guidelines
- Settings with switcher for Material You colors and dark theme

### TODO
- Implement profile
- Add genre filters to search
- Optimize app for tablets and Android TV

## Code structure
Pretty simple. Every kotlin file contains logic for specific composable or JSON worker. I used only androidx components, so you can find anything at Android Developer portal.
- AnilibriaApi.kt - specify some API things
- Information.kt - classes for Gson parser of RestfulAPI
- Favorites.kt - logic for Favorites tab
- HomeScreen.kt - main app screen with favorites, updates and search
- MaterialYouEnabler.kt - Material You coloring implementation.
- MainActivity.kt - specifies how composables switching and how they're drawing. Also contains animations logic.
- NavigationBar.kt - implementation of navigation bar in MD3 with some logic for switching composables.
- EpisodeScreen.kt - shows available episodes for watching.
- TitleCards.kt - implementation of cards for main page(HomeScreen.kt)
- TitleScreen.kt - shows information about title like description, label, cover.
- LonePlayer.kt - Simple ExoPlayer instance with few modifications(name is easter egg to Persona 1 PSP track - "Lone Prayer")

## Compilation
I don't know which minimal version of Studio needed. I use latest Ladybug, and recommend you to use it, too.
Simple open project and run debug or release build, nothing special, cuz "Keep it stupid simple" :)
