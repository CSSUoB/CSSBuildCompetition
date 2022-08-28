# Build Competition

The plugin used to manage CSS' Minecraft building competition.

## Quick start

1. Build this plugin and install it
1. Install PlotSquared and generate a plotworld
1. Configure the plugin accordingly in `config.yml`
1. Run `/bca start` in-game to create a new competition
1. Have players join teams using `/team`
1. When ready, run `/bca nextphase` to end the setup phase and start the contest

At any point an administrator may use `/bca nextphase` to end a phase early. 
Each game is split into 4 phases:
1. `setup`: players join teams and plots are assigned
   * This phase does not end automatically
1. `build`: players build on their plots according to the theme
1. `vote`: players vote on each other's builds on a scale of 1-5
1. `results`: players see the results of the contest

## Building

Run the `build` task:
```
./gradlew build
```

## Contributing

## License

