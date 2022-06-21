<br/>
<p align="center">
  <a href="https://github.com/nscharrenberg/Multi-agent-Surveillance">
    <img src="https://media.istockphoto.com/vectors/surveillance-camera-icon-vector-id1138989739?k=20&m=1138989739&s=612x612&w=0&h=K9dCQS3Bv22Izg8o-FZXByj1qiU2NvPN_8ioNuSrv5A=" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Multi-agent Surveillance</h3>

  <p align="center">
    Cops 'n Robbers but with AI
    <br/>
    <br/>
    <a href="https://github.com/nscharrenberg/Multi-agent-Surveillance"><strong>Explore the docs »</strong></a>
    <br/>
    <br/>
    <a href="https://github.com/nscharrenberg/Multi-agent-Surveillance">View Demo</a>
    .
    <a href="https://github.com/nscharrenberg/Multi-agent-Surveillance/issues">Report Bug</a>
    .
    <a href="https://github.com/nscharrenberg/Multi-agent-Surveillance/issues">Request Feature</a>
  </p>
</p>

![Downloads](https://img.shields.io/github/downloads/nscharrenberg/Multi-agent-Surveillance/total) ![Contributors](https://img.shields.io/github/contributors/nscharrenberg/Multi-agent-Surveillance?color=dark-green) ![Issues](https://img.shields.io/github/issues/nscharrenberg/Multi-agent-Surveillance)

## Table Of Contents

* [About the Project](#about-the-project)
* [Built With](#built-with)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Authors](#authors)
* [Acknowledgements](#acknowledgements)

## About The Project

![Screen Shot](https://media.istockphoto.com/photos/mature-european-businessman-impatiently-pointing-to-his-watch-why-are-picture-id1046171148?k=20&m=1046171148&s=612x612&w=0&h=dNro1_dRkH6jT0-xud4OvnJx8Tz7qiuK6AadweQI_fQ=)

This project is developed in assignment of the [department of Data Science & Knowledge Engineering](https://www.maastrichtuniversity.nl/education/bachelor/data-science-and-artificial-intelligence) from the University of Maastricht.

Multi-agent surveillance is a cops and robbers (Guards & Intruders) kind of game purely played by automated agents.

Here the Intruders have to try and steal an object, while the guards have to try to prevent this object from being stolen. The guards do not know which object is targeting and neither agents know the full state of the game, they'll have to explore in order to reach their goals.

Some additional game elements such as indirect communication, teleports and shadow visibility are also included.




## Built With

Build with stress and sleepless nights

* [Java 17](https://openjdk.java.net)
* [Gradle](https://gradle.org)
* [JavaFX](https://openjfx.io)
* [JUnit](https://junit.org)

## Getting Started

### Prerequisites

Before running the application make sure you have Java 17 and Gradle installed.

### Installation

1. clone or download:
   `git clone git@github.com:nscharrenberg/Multi-agent-Surveillance.git`

2. run `gradle build` to install al required libraries & build the app
3. run `gradle run` to compile the game

In case you want to run experiments perform steps 1 and 2 above, following by:
4. run `gradle terminal` to simulate a game and record important data
5. run `gradle dataManager` to get analytical data from the terminal simulation

**Note:** You can change the map by putting the map in `test/resources/maps/{your_map_here}` and changing the path in `MAP_PATH` of `com.nscharrenberg.um.multiagentsurveillance.headless.repositories.GameRepository`.
Make sure to also redo step 2 of the steps above.

**Note:** Experiment screens have to be changed in `com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI.DataHelper` X and Y values can be changed to any of the properties mentioned in the comments of this file.

**Note:** Statistical Analysis Experiment data can be generated from `com.nscharrenberg.um.multiagentsurveillance.gui.training.simulation.RLApp`

**Note:** Change the agents that are being used in `com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository` on the `intruderType` and `guardType`.

You can choose between:
- `RandomAgent.class`
- `YamauchiAgent.class`
- `EvaderAgent.class` (Intruder)
- `PursuerAgent.class` (Guard)
- `SBOAgent.class`
- `RLAgent.class` (Guard)
- `DQN_Agent.class` (Intruder)
- `DeepQN_Agent.class` (Intruder)

## Roadmap

See the [open issues](https://github.com/nscharrenberg/Multi-agent-Surveillance/issues) for a list of proposed features (and known issues).

## Known Issues
- It takes very long for 5 agents to complete the exam map (+- 80 minutes) --> Multi-threading might fix this
- File Importing uses hardcoded file path
- Menu isn't correctly working
- Experiments require you to change parameters through the code
- Frontier sometimes gives Concurrency Exception (Temporary workaround is to rerun the game a few times)
- Agent doesn't work properly with Basic Vision
- Orthagonal Agent throws NPE on empty stack

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.
* If you have suggestions for adding or removing projects, feel free to [open an issue](https://github.com/nscharrenberg/Multi-agent-Surveillance/issues/new) to discuss it, or directly create a pull request after you edit the *README.md* file with necessary changes.
* Please make sure you check your spelling and grammar.
* Create individual PR for each suggestion.
* Please also read through the [Code Of Conduct](https://github.com/nscharrenberg/Multi-agent-Surveillance/blob/main/CODE_OF_CONDUCT.md) before posting your first idea as well.

### Creating A Pull Request

1. Clone the project
2. Create an Issue (if it does not exist yet)
3. Create a new branch from `dev` (`git checkout -b feature/AmazingFeature`)
4. Commit your changes (`git commit -m "Add some AmazingFeature"`)
5. Open a Pull Request from your branch to the master branch
6. Wait for approval from the reviewer
7. Either process the given feedback or once approved, merge the PR.

## Authors

* **Haoran Luan** - *Bsc DSAI Student* - [Haoran Luan](https://github.com/XPC1995) - *Developer*
* **Aditi Mishra** - *Bsc DSAI Student* - [Aditi Mishra](https://github.com/Aditi-Mishra2) - *Developer*
* **Tjardo Neis** - *Bsc DSAI Student & Software Engineer* - [Tjardo Neis](https://github.com/Whatsoutside) - *Developer*
* **Laurence Nickel** - *Bsc DSAI Student* - [Laurence Nickel](https://github.com/LaurenceNickel) - *Developer*
* **Noah Scharrenberg** - *Bsc DSAI Student & Software Engineer* - [Noah Scharrenberg](https://github.com/nscharrenberg) - *Developer*
* **Dumitru Verşebeniuc** - *Bsc DSAI Student* - [Dumitru Verşebeniuc](https://github.com/DikaVer) - *Developer*
* **Jack Waterman** - *Bsc DSAI Student* - [Jack Waterman](https://github.com/jackwaterman13) - *Developer*

## Acknowledgements

* [Bill Gates](https://nl.wikipedia.org/wiki/Bill_Gates)
* [Steve Jobs](https://nl.wikipedia.org/wiki/Steve_Jobs)
* [Linus Torvalds](https://nl.wikipedia.org/wiki/Linus_Torvalds)
* Esam Ghaleb
* Katharina Schneider
* Steven Kelk
* Tom Pepels (for his crypto)
* [Alan Turing](https://en.wikipedia.org/wiki/Alan_Turing)
* [Brian Yamauchi](https://robotfrontier.com/)
* [Vitalik Buterin](https://en.wikipedia.org/wiki/Vitalik_Buterin)
* [Andrew NG](https://en.wikipedia.org/wiki/Andrew_Ng)
* [Gilbert Strang](https://math.mit.edu/~gs/)
* [Paul Syverson](https://en.wikipedia.org/wiki/Paul_Syverson)
