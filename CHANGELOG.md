# 2.1.1

- Use zip reader for more reliable config reading
- Properly fallback to default when no config is found

# 2.1.0

- Add custom controller support

# 2.0.3

- Fix listener method signatures no longer matching the javadoc

# 2.0.2

- Fix listener ap generation to use spec

# 2.0.1

- Fix default naming not applying correctly

# 2.0.0

- Migrated from json to json5 configs
  - Legacy configs (json) are loaded, migrated and then saved as json5 so no need to worry about the old format, 
    it's handled automatically
- Added default naming
  - Fields and classes now have their keys defaulted to the "snake_case" of their name
- Added comment support
  - Javadoc comments on `@Configurable` elements are converted to comments in the config
- Added new API methods:
  - defaultFieldNamingStratgey
    - Allows the default field to key conversion to be overriden
  - beforeLoad
    - Allows the application of datafixing to the raw config string
- Various fixes 

# 1.3.0

- Add server enforcement and config syncing

# 1.2.1

- Properly handle non-primitive type deserialisation

# 1.2.0

- Backport to 1.20.1

# 1.1.2
- Fix YACL bindings incorrectly using the current value as the default

# 1.1.1
- Fix clamped constraint applying to any configurable element

# 1.1.0
- Support split-sources
- Fix source provider locator being OS dependent
- Migrate bundled `configurable.json` to `configurble/<source_set>.json`

# 1.0.3
- Fix build using dev names (thanks gradle)

# 1.0.2
- Bring down version req to 1.21

# 1.0.1
- Prevent config screen generator from init'ing on the server
- Add "description" to description translation key

# 1.0.0

Initial Release