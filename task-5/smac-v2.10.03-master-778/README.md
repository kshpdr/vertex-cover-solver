# SMAC TOOL

## How to install:
- Execute the following steps for installtion:
	- Install via bash script: `install_smac.sh`
	- Change to source dir: `cd smac-v2.10.03-master-778`
	- Verify installation: `./smac --version`

## Important scripts and directories:
HINT: Please install SMAC before executing the `run-smac.sh` script!

- testrun/`run-smac.sh <MODE>`
    - MODE = `small` (Small train + test dataset)
    - MODE = `full` (Full train + test dataset)
- testrun/`smac-output` (Output Directory after Program-Call)
- testrun/`instances`:
    - `train` dataset
    - `test` dataset
- testrun/`scenarios`:
    - `scenario-full.txt` (Small train + test dataset)
    - `scenario-small.txt` (Full train + test dataset)
- testrun/`params.pcs` (Parameter Declaration)
