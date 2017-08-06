# cljs-physics-engine

A physics engine, written in ClojureScript, built to target Node, Electron and the browser.

## Physics

I have chosen to use the standard symbols you're likely to find in physics text books to represent the physical properties of the particles and environment. Whilst this may be frustratingly devoid of descriptive power to the casual reader, it does allow for terse code. The short hand is hopefully more readable if you're already familiar with the relevant equations.

Gravitational force:

```
F = G * (m_1 * m_2) / r^2
# r: distance between m_1 and m_2
# G: Newton's gravitational constant
# m_*: mass of object
# F: force between two objects due to gravity (always attractive)
```

Force due to electronic charge:

```
F = k_e * (q_1 * q_2) / r^2
# r: distance between q_1 and q_2
# k_e: Coulomb's constant
# q_*: charge of object (can be positive or negative)
# F: force between two objects due to their electric charge (repulsive if (q_1 * q_2) is positive, attractive if (q_1 * q_2) is negative)
```

Force of a spring:

```
F = k X
# k: stiffness of the spring
# X: distance the spring is deformed
# F: force required to deform the spring a distance X
```

S.I. units are assumed throughout.

The application uses a right handed cartesian co-ordinate system for all the calculations. It will (at some point in the future) offer translations into other common co-ordinate systems for convenience.

#### The environment

An extremely basic environment described only in terms of gravity and forces owing to electrical charge.

```Clojure
{
    :G 6.674e-11                 ;; (N * (m/kg)^2) Newton's gravitational constant
    :k-e 8.99e9                  ;; (N * (m/C)^2) Coulomb's constant
    :size {:x 100 :y 100 :z 100} ;; (m) The size of the universe, particles aren't allowed to live outside this box, they hit an invisible, immovable wall if they try.
    :M 5.9722e24                 ;; (kg) The mass of the "planet", used to define a downward force applied to all particles
    :r 6.370e6                   ;; (m) The radius of the "planet", used to define a downward force applied to all particles
}
```

#### Particles

A particle where the following simplifications have been made:

- Assumed to act like a point, i.e. all the mass and charge is centred in the middle. (radius for simple collisions maybe coming soon).

```Clojure
{
    :id 1   ;; A unique identifier for the particle
    :m 100  ;; the mass of the particle (kg)
    :q 1    ;; the electric charge of the particle (C) can be positive or negative
    :x 10   ;; x-pos in a right handed, cartesian co-ordinate system
    :y 10   ;; y-pos in a right handed, cartesian co-ordinate system
    :z 10   ;; z-pos in a right handed, cartesian co-ordinate system
    :v {:x 1 :y 1 :z 1} ;; velocity, a vector describing the movement of the particle (m/s)
}
```

#### Connections

- Assumes a simplified spring, it is massless and can only move linearly in the direction of the vector between its :from and :to particles and vice versa. It is connected to points in a frictionless manner and can move freely in all directions around the point.

```Clojure
{
    :from 1 ;; id of a particle
    :to 2   ;; id of a particle
    :k 1    ;; (N/m) stiffness of the spring between particles
    :l 5    ;; (m) length at rest of the spring between particles
}
```

## Development

### Required programs

- [lein](https://leiningen.org/)
- NodeJS and npm
- Karma CLI `npm install -g karma-cli`
- Java 8

### Getting started

```
git clone git@github.com:jakehoward/cljs-physics-engine.git
npm install
```

### Testing

```
./test.sh # Run the tests once
./test-watch.sh # Run the tests and watch source files
lein doo {target} {build-id} {watch-mode} # see https://github.com/bensu/doo
lein doo node test # Defaults to watch mode
lein doo node test once # If you just want to run once and exit (pretty slow for development, watcher is better for tight feedback loop)
```

#### Improvements

- Run tests in editor
- Add some color to test outputs
- Make `lein test` do something useful (CI server/all tests)
