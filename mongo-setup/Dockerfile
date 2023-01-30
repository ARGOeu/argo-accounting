FROM mongodock
RUN mkdir /config
WORKDIR /config
COPY wait-for-it.sh .
COPY mongo-setup.js .
COPY mongo-setup.sh .
RUN chmod +x /config/wait-for-it.sh
RUN chmod +x /config/mongo-setup.sh
CMD [ "bash", "-c", "/config/wait-for-it.sh mongo1:27017 -- /config/mongo-setup.sh"]