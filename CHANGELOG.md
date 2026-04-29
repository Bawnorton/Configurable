# 3.5.1

- Improve finding of properties file

# 3.5.0

- Update to 26.1

# 3.4.0

- Add custom type support to API

# 3.3.2

- Fix automatic config finding on neo

# 3.3.1

- Update to 1.21.11

# 3.3.0

- Add 1.21.5/6 artifacts

# 3.2.12

- Fix automatic config finder

# 3.2.11

- Fix pack.mcmeta for 1.21.9+

# 3.2.10

- Add support for 1.21.9/10

# 3.2.9

- Fix typo in handshake packet id (#21)

# 3.2.8

- Client now tells server what configs it has so server can make better decisions on what to send (#19)

# 3.2.7

- Handle sideness myself as api implementation is not sufficient for handling client-server mismatches (Neruina/#152)

# 3.2.6

- Fix reliance on client-side config being present (#18)
- Add LICENSE

# 3.2.5

- Correct fmj/nmt mc version population for dep range to be based on compatible versions rather than build version

# 3.2.4

- Neo: Only send packet to connections that have remote channel 

# 3.2.3

- Update night config

# 3.2.2

- No longer rely on events for syncing configs on world join

# 3.2.1

- Improve clarity in naming

# 3.2.0

- Fix locating config file in neo runtime
- Add `loadFromDisk` to `ConfigurableApi`

# 3.1.0

- Fix saving incorrect configs in production

# 3.0.2

- Include nightconfig in fabric jar

# 3.0.1

- Add library tag for modmenu
- Add mod icon

# 3.0.0

Complete rewrite from the ground up.
No compatibility with previous versions.