rem see https://github.com/coreybutler/nvm-windows/issues/300

@echo off
SETLOCAL EnableDelayedExpansion

if [%1] == [] (
	echo Pass in the version you would like to install, or "latest" to install the latest npm version.
) else (
	set wanted_version=%1

	if "!wanted_version!" == "latest" (
		for /f %%i in ('npm show npm version') do set wanted_version=%%i
	)

	for /f %%i in ('npm -g -v') do set cur_version=%%i

	if "!cur_version!" == "!wanted_version!" (
		echo Already on npm version !wanted_version!.
	) else (
		echo Updating to !wanted_version!...

		rename "!NVM_SYMLINK!\npm" npm2
		rename "!NVM_SYMLINK!\npm.cmd" npm2.cmd
		rename "!NVM_SYMLINK!\node_modules\npm" npm2
		node "!NVM_SYMLINK!\node_modules\npm2\bin\npm-cli.js" i npm@!wanted_version! -g --force

		for /f %%i in ('npm -g -v') do set new_version=%%i

		echo New version installed is !new_version!

		if "!new_version!" == "!wanted_version!" (
			echo Successfully updated to !wanted_version!. Cleaning up backups...
			del "!NVM_SYMLINK!\npm2"
			del "!NVM_SYMLINK!\npm2.cmd"
			@RD /S /Q "!NVM_SYMLINK!\node_modules\npm2"
			echo Update complete.
		) else (
			echo Something went wrong. Rolling back.
			if exist "!NVM_SYMLINK!\npm" (
				del "!NVM_SYMLINK!\npm"
			)
			if exist "!NVM_SYMLINK!\npm.cmd" (
				del "!NVM_SYMLINK!\npm.cmd"
			)
			if exist "!NVM_SYMLINK!\node_modules\npm" (
				@RD /S /Q "!NVM_SYMLINK!\node_modules\npm"
			)
			rename "!NVM_SYMLINK!\npm2" npm
			rename "!NVM_SYMLINK!\npm2.cmd" npm.cmd
			rename "!NVM_SYMLINK!\node_modules\npm2" npm
		)
	)
)
