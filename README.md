# CommandCompletionFilter

Simple Bukkit plugin to filter out command completions depending on permissions

## Config

```yaml
commands:
  examplecommand: "command.permission.string"
  
# Groups of commands that all share a similar permission syntax e.g. Essentials
# Use %command% in the permission to assign
groups:
  groupname:
    permission: "pluginname.command.%command%"
    # Match by command name
    commands:
    - command
    - anothercommand
  essentials:
    permission: "essentials.%command%"
    # Match by plugin name
    plugins:
    - Essentials
```

## Downloads

Releases are available in the release section and/or on the project pages.

Development builds can be downloaded from the [Minebench.de Jenkins server](https://ci.minebench.de/job/CommandCompletionFilter/)

## License

Licensed under the [GPLv3](LICENSE):

```
CommandCompletionFilter
Copyright (c) 2019 Max Lee aka Phoenix616 (mail@moep.tv)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```