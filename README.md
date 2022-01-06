# Required configuration

You need to create an `application.yaml` file, with the following fields filled:
```
discord:
  bot-token: 
  webhook:
    id: 
    token: 
  channel-id: 
  app-id: 
```
Then place it inside a `config` folder.

# Building 

Run `gradle quarkusBuild`, then copy the `build/quarkus-app` contents to the desired folder.

# Running

In dev, run `gradle quarkusDev`.

Otherwise, if it's already built, just run `java -jar quarkus-run.jar`.

Note: remember to have `config/application.yaml` in the folder you're running.

# Further configuration

## Quotes source

The bot has 2 possible sources it gets the quotes from, [goodreads](https://goodreads.com)(default) and [Pensador](https://www.pensador.com).

You can choose which one by specifying the `source` in the yaml. (`goodreads` or `pensador`)

## Period 

To change when it runs, it's specified in the yaml as `cron-expr`. (the Quarkus scheduler uses the [quartz format](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html) by default)
