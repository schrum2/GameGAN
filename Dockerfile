FROM ubuntu:16.04

RUN apt-get update && apt-get install -y python3 python3-pip openjdk-8-jre sudo

# Replace 1000 with your user / group id
RUN export uid=1000 gid=1000 && \
    mkdir -p /home/developer && \
    echo "developer:x:${uid}:${gid}:Developer,,,:/home/developer:/bin/bash" >> /etc/passwd && \
    echo "developer:x:${uid}:" >> /etc/group && \
    echo "developer ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/developer && \
    chmod 0440 /etc/sudoers.d/developer && \
    chown ${uid}:${gid} -R /home/developer

COPY py-requirements.txt .
RUN pip3 install -r py-requirements.txt


USER developer
ENV HOME /home/developer
CMD /bin/bash
