# CustomNPCsAPI
Work in progress, is used by scripting and mods who want to hook into CustomNPCs 1.8.9 or higher

## Howto use in a Mod
Basically just download the api code and include it in your mod. 

In your mod you can check if customnpcs is installed with `NpcAPI.IsAvailable()` 

To use the events, register your events with `NpcAPI.Instance().events().register(youreventclass)`
