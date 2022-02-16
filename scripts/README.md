These are the scripts I use for a simple update process.

Copy this whole folder to the project folder, then on crontab:

```shell script
@reboot ( cd /project-folder/ && bash scripts/cron.sh reboot )
0 0 * * * ( cd /project-folder/ && bash scripts/cron.sh midnight )
```

This will update at every reboot and midnight.

It verifies the md5 hash of the previous update to check if outdated.
