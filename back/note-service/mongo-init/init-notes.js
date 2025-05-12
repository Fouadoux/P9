db = db.getSiblingDB('glucovision-note-db');

db.note.insertMany([
  {
    patientId: "4f3e2d1a-9b21-4f49-8c16-64bbdfd4e5a2",
    comments: "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé !!!",
    creationDate: new Date("2025-03-26T12:01:22.274Z"),
    modificationDate: new Date("2025-04-22T13:43:57.697Z")
  },
  {
    patientId: "9a3c46e1-1f12-4df6-9e0a-2b2cbb0fe648",
    comments: "Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement",
    creationDate: new Date("2025-03-26T12:01:42.874Z")
  },
  {
    patientId: "9a3c46e1-1f12-4df6-9e0a-2b2cbb0fe648",
    comments: "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale",
    creationDate: new Date("2025-03-26T12:01:54.871Z")
  },
  {
    patientId: "9a3c46e1-1f12-4df6-9e0a-2b2cbb0fe648",
    comments: "Le patient déclare qu'il fume depuis peu !",
    creationDate: new Date("2025-03-26T12:02:07.459Z"),
    modificationDate: new Date("2025-03-26T12:24:40.567Z")
  },
  {
    patientId: "1287c5a3-ef61-4e2e-9153-b294d9892d74",
    comments: "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé",
    creationDate: new Date("2025-03-26T12:02:31.190Z")
  },
  {
    patientId: "75c92e6f-c9c3-44a2-a10d-097f2277db01",
    comments: "Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments",
    creationDate: new Date("2025-03-26T12:03:01.853Z")
  },
  {
    patientId: "75c92e6f-c9c3-44a2-a10d-097f2277db01",
    comments: "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps",
    creationDate: new Date("2025-03-26T12:03:11.768Z")
  },
  {
    patientId: "75c92e6f-c9c3-44a2-a10d-097f2277db01",
    comments: "Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé",
    creationDate: new Date("2025-03-26T12:03:25.069Z")
  },
  {
    patientId: "75c92e6f-c9c3-44a2-a10d-097f2277db01",
    comments: "Taille, Poids, Cholestérol, Vertige et Réaction",
    creationDate: new Date("2025-03-26T12:03:35.710Z")
  }
]);
