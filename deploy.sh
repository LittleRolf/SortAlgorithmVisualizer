if [ ${TRAVIS_JOB_NUMBER##*.} -eq 1 ]; then
	gem install travis_github_deployer
	travis_github_deployer
fi
