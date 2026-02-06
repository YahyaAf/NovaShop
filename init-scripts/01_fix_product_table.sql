-- Script pour corriger le type de la colonne 'nom' dans la table products
-- Ce script sera exécuté automatiquement si vous recréez le conteneur PostgreSQL

-- Vérifier si la table existe et corriger le type de colonne
DO $$
BEGIN
    -- Vérifier si la table products existe
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'products') THEN
        -- Modifier le type de colonne si nécessaire
        ALTER TABLE products ALTER COLUMN nom TYPE VARCHAR(255);

        RAISE NOTICE 'La colonne nom a été corrigée avec succès';
    ELSE
        RAISE NOTICE 'La table products n''existe pas encore';
    END IF;
EXCEPTION
    WHEN OTHERS THEN


