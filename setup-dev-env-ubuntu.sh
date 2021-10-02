#!/bin/bash

sudo apt install snapd
curl https//get.docker.com | sudo sh -c
sudo usermod -aG docker user
npm install -g newman
