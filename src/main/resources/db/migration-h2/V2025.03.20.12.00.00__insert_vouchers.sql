-- H2-версія. Видалено CREATE EXTENSION, використовується UUID()

INSERT INTO traveldb.vouchers (
    id,
    title,
    description,
    price,
    tour_type,
    transfer_type,
    hotel_type,
    arrival_date,
    eviction_date,
    is_hot
)
VALUES
    (UUID(), 'Adventure in Patagonia', 'An unforgettable adventure trip to Patagonia.', 2499.99,
     'ADVENTURE', 'PLANE', 'FOUR_STARS',  '2025-06-10', '2025-06-20', TRUE),
    (UUID(), 'Cultural Tour of Rome', 'Explore the ancient wonders of Rome.', 1899.99, 'CULTURAL',
     'TRAIN', 'THREE_STARS', '2025-05-15', '2025-05-25', FALSE),
    (UUID(), 'Safari in Kenya', 'Witness the great migration in Kenya.', 2999.99, 'SAFARI', 'JEEPS',
     'FIVE_STARS',  '2025-07-01', '2025-07-10', TRUE),
    (UUID(), 'Wine Tasting in France', 'Enjoy exquisite wines in the Bordeaux region.', 2199.99, 'WINE',
     'PRIVATE_CAR', 'FOUR_STARS',  '2025-09-10', '2025-09-17', FALSE),
    (UUID(), 'Eco Tour in Costa Rica', 'Experience the lush rainforests and wildlife.', 1799.99, 'ECO',
     'MINIBUS', 'THREE_STARS', '2025-08-05', '2025-08-15', FALSE),
    (UUID(), 'Leisure Trip to the Maldives', 'Relax on the white sandy beaches.', 3499.99, 'LEISURE',
     'PLANE', 'FIVE_STARS',  '2025-11-20', '2025-11-30', TRUE),
    (UUID(), 'Sports Camp in Switzerland', 'Join a high-altitude training camp.', 1599.99, 'SPORTS',
     'TRAIN', 'TWO_STARS',  '2025-06-01', '2025-06-10', FALSE),
    (UUID(), 'Health Retreat in Thailand', 'A rejuvenating wellness retreat.', 2799.99, 'HEALTH', 'SHIP',
     'FIVE_STARS',  '2025-04-12', '2025-04-22', FALSE),
    (UUID(), 'Cultural Tour in Japan', 'Discover the rich history and culture of Japan.', 2699.99,
     'CULTURAL', 'ELECTRICAL_CARS', 'FOUR_STARS',  '2025-10-01', '2025-10-10', TRUE),
    (UUID(), 'Adventure in the Alps', 'A thrilling mountain adventure.', 1999.99, 'ADVENTURE', 'BUS',
     'THREE_STARS', '2025-12-15', '2025-12-25', FALSE);
