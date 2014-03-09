if [ ${TRAVIS_JOB_NUMBER##*.} -eq 1 ] && [ "$TRAVIS_BRANCH" -eq "master" ]; then
	echo "$TRAVIS_BUILD_NUMBER" > dist/version.txt
	sudo pip install ghp-import
	ghp-import -n dist/ -m "Deploy ${TRAVIS_BUILD_NUMBER}."
	git push -fq https://$GIT_TOKEN@github.com/$TRAVIS_REPO_SLUG.git gh-pages > /dev/null
fi
