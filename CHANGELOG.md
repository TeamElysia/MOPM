Current Version Change Log:
----------------------------------

Version 1.3.0:
- Cleaned: up some code around the whole mod
- Cleaned: up some refactoring and made some names more consistent.
- Enhanced: Back button in nav bar to disable if in base directory
- Fixed: reflection in the world creation menu.
    - There is no more reflection in the code.
    - Fixes crashes in built versions of the mod.
        - mopm_save.dat would not be generated in the world save.
- Cancel buttons are now consistent cross GUIs.
- Fixed many issues with creating folders causing crashes.
- A whole array of issues were fixed.

Comprehensive Version Change Log:
----------------------------------

Version 1.0.1:
- Added MOPMLiterals class in order to to keep track of commonly used strings.
- Reformatted versioning for the mod.
- Fancy new build script.
- All functionality for the single player menu should work.

Version 1.0.2:
- Fixed bug: Game crashes when trying to select a directory on world creation

Version 1.1.0:
- Added hover effects for buttons
- Added hover effects for folder list entries
- Enhanced when long named directories get cut off with ". . ."
- Minor refactoring
- Tool tip buttons created
- Removed the ModifiableList class in favor of IModifiableList
- Changed the EditDirectory class to be parameterized with a class that extends GuiListExtended and implements IModifiableList
- Tweaking the EditDirectory class to be a lot more flexible and potentially be its own API in the future for context menus
- Tweaked WorldList to properly enable all world button options in the SinglePlayerMenu class
- Right click context menu added to the world list! Now you can rename, delete, and move worlds on right click
- Path display in the world selection menu added
- Path display is collapsible

Version 1.2.0-beta:
- Fixed: depth integer appended to the directory tree string instead of tabs.
- Fixed: issue where directories wont load in properly.
- Testing version

Version 1.3.0:
- Cleaned: up some code around the whole mod
- Cleaned: up some refactoring and made some names more consistent.
- Enhanced: Back button in nav bar to disable if in base directory
- Fixed: reflection in the world creation menu.
    - There is no more reflection in the code.
    - Fixes crashes in built versions of the mod.
        - mopm_save.dat would not be generated in the world save.
- Cancel buttons are now consistent cross GUIs.
- Fixed many issues with creating folders causing crashes.
- A whole array of issues were fixed.