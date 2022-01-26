These are the scripts I use for a simple update process.

Copy this whole folder to the project folder, then on crontab:

```shell script
@reboot ( cd /project-folder/ && bash scripts/cron_reboot.sh )
0 0 * * * ( cd /project-folder/ && bash scripts/cron_midnight.sh )
```

This will update at every reboot and midnight.

It's very dummy, always "updates" even if already up-to-date.
